package com.pontual_telemetria.pontual_monitor_api.domain.service;

import com.pontual_telemetria.pontual_monitor_api.domain.model.monitoring.ControlReading;
import com.pontual_telemetria.pontual_monitor_api.domain.model.monitoring.InstantaneousFlowRate;
import com.pontual_telemetria.pontual_monitor_api.domain.model.regulatory.UsageGrant;
import com.pontual_telemetria.pontual_monitor_api.domain.model.regulatory.UsageGrantMonthly;
import com.pontual_telemetria.pontual_monitor_api.domain.repository.ControlReadingDataRepository;
import com.pontual_telemetria.pontual_monitor_api.domain.repository.InstantaneousFlowRateRepository;
import com.pontual_telemetria.pontual_monitor_api.domain.repository.UsageGrantRepository;
import com.pontual_telemetria.pontual_monitor_api.web.dto.dashboard.DailyVolumeDTO;
import com.pontual_telemetria.pontual_monitor_api.web.dto.dashboard.InfoPanelDTO;
import com.pontual_telemetria.pontual_monitor_api.web.dto.dashboard.MonthlyVolumeDTO;
import com.pontual_telemetria.pontual_monitor_api.web.dto.dashboard.UsageGrantDashboardInfoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardDomainService {

    private final InstantaneousFlowRateRepository instantaneousFlowRateRepository;
    private final ControlReadingDataRepository controlReadingDataRepository;
    private final UsageGrantRepository usageGrantRepository;

    public InfoPanelDTO dashboardInfo(Long externalId) {
        InfoPanelDTO infoPanelDTO = new InfoPanelDTO();
        infoPanelDTO.setLocationId(externalId);

        List<MonthlyVolumeDTO> monthlyVolume = buildMonthlyVolume(externalId);
        List<DailyVolumeDTO> dailyVolumes   = buildDailyVolume(externalId);

        infoPanelDTO.setMonthlyVolumeList(monthlyVolume);
        infoPanelDTO.setDailyVolumeList(dailyVolumes);
        infoPanelDTO.setUsageGrantDashboardInfo(buildUsageGrantVolumeInfo(externalId, dailyVolumes));

        return infoPanelDTO;
    }

    private List<MonthlyVolumeDTO> buildMonthlyVolume(Long externalId) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yyyy");

        List<ControlReading> controlReading =
                controlReadingDataRepository.findAllByExternalId(externalId);

        if (controlReading == null || controlReading.isEmpty()) {
            return List.of();
        }

        Map<YearMonth, BigDecimal> sumByMonth = controlReading.stream()
                .filter(cr -> cr != null && cr.getDtReading() != null)
                .collect(Collectors.groupingBy(
                        cr -> YearMonth.from(cr.getDtReading()),
                        TreeMap::new,
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                cr -> normalizeValue(cr.getReadingValue()),
                                BigDecimal::add
                        )
                ));

        return sumByMonth.entrySet().stream()
                .map(e -> new MonthlyVolumeDTO(
                        e.getKey().format(formatter),
                        e.getValue()
                ))
                .toList();
    }

    private List<UsageGrantDashboardInfoDTO> buildUsageGrantVolumeInfo(
            Long externalId,
            List<DailyVolumeDTO> dailyVolumes
    ) {
        Map<YearMonth, BigDecimal> opHoursByMonth = dailyVolumes.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(
                        d -> YearMonth.from(LocalDate.parse(d.getDay())),
                        TreeMap::new,
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                d -> normalizeValue(d.getDailyOperationHours()),
                                BigDecimal::add
                        )
                ));

        return usageGrantRepository.findFirstByExternalIdOrderByStartDateDesc(externalId)
                .map(usageGrant -> {
                    LocalDate startDate = usageGrant.getStartDate().toLocalDate();
                    YearMonth startYm = YearMonth.from(startDate);

                    List<UsageGrantMonthly> months = usageGrant.getMonthlyGrants().stream()
                            .filter(Objects::nonNull)
                            .sorted(Comparator.comparing(
                                    UsageGrantMonthly::getMonth,
                                    Comparator.nullsLast(Integer::compareTo)))
                            .toList();

                    BigDecimal totalDuration = months.stream()
                            .map(m -> normalizeValue(m.getHoursDay()).multiply(normalizeValue(m.getDaysMonth())))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    BigDecimal totalVolume = months.stream()
                            .map(m -> normalizeValue(m.getMaximumVolume()))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    BigDecimal averageFlow = totalDuration.signum() == 0
                            ? BigDecimal.ZERO
                            : totalVolume.divide(totalDuration, 6, RoundingMode.HALF_EVEN);

                    return months.stream()
                            .map(m -> {
                                int monthIndex = normalizeValue(BigDecimal.valueOf(
                                        Optional.ofNullable(m.getMonth()).orElse(1))).intValue();

                                YearMonth ym = startYm.plusMonths(monthIndex - 1L);

                                BigDecimal monthlyOperationHours = opHoursByMonth
                                        .getOrDefault(ym, BigDecimal.ZERO)
                                        .setScale(1, RoundingMode.HALF_UP);

                                BigDecimal monthlyUsageGrantVolume = normalizeValue(m.getMaximumVolume());

                                BigDecimal hoursDay  = normalizeValue(m.getHoursDay());
                                BigDecimal daysMonth = normalizeValue(m.getDaysMonth());
                                BigDecimal monthDuration = hoursDay.multiply(daysMonth);

                                BigDecimal maxMonthlyOperationHours = monthDuration
                                        .setScale(1, RoundingMode.HALF_UP);

                                return new UsageGrantDashboardInfoDTO(
                                        monthIndex,
                                        monthlyUsageGrantVolume,
                                        monthDuration,
                                        monthlyOperationHours,
                                        maxMonthlyOperationHours,
                                        totalVolume,
                                        totalDuration,
                                        averageFlow
                                );
                            })
                            .toList();
                })
                .orElseGet(List::of);
    }

    private List<DailyVolumeDTO> buildDailyVolume(Long externalId) {
        List<ControlReading> readings =
                controlReadingDataRepository.findAllByExternalId(externalId);

        if (readings == null || readings.isEmpty()) return List.of();

        Map<Integer, BigDecimal> hoursDayByMonth = usageGrantRepository
                .findFirstByExternalIdOrderByStartDateDesc(externalId)
                .map(usageGrant -> usageGrant.getMonthlyGrants().stream()
                        .filter(Objects::nonNull)
                        .collect(Collectors.toMap(
                                UsageGrantMonthly::getMonth,
                                m -> Optional.ofNullable(m.getHoursDay()).orElse(BigDecimal.ZERO),
                                (a, b) -> a
                        ))
                ).orElseGet(Collections::emptyMap);

        List<ControlReading> sorted = readings.stream()
                .filter(r -> r != null && r.getDtReading() != null && r.getReadingValue() != null)
                .sorted(Comparator.comparing(ControlReading::getDtReading))
                .toList();

        Map<LocalDate, BigDecimal> lastValueByDay = new TreeMap<>();
        Map<LocalDate, LocalDateTime> firstTsByDay = new HashMap<>();
        Map<LocalDate, LocalDateTime> lastTsByDay = new HashMap<>();

        for (ControlReading r : sorted) {
            LocalDateTime ts = r.getDtReading();
            LocalDate day = ts.toLocalDate();

            lastValueByDay.put(day, normalizeValue(r.getReadingValue()));
            firstTsByDay.putIfAbsent(day, ts);
            lastTsByDay.put(day, ts);
        }

        List<DailyVolumeDTO> values = new ArrayList<>(lastValueByDay.size());
        BigDecimal prevLast = null;

        for (var e : lastValueByDay.entrySet()) {
            LocalDate day = e.getKey();
            BigDecimal last = normalizeValue(e.getValue());

            BigDecimal daily = (prevLast == null) ? last : last.subtract(prevLast);
            if (daily.signum() < 0) daily = BigDecimal.ZERO;

            YearMonth ym = YearMonth.from(day);
            BigDecimal instantaneousFlowRate = resolveInstantaneousFlowRate(externalId, ym);
            BigDecimal calculatedDailyMeasure = daily.multiply(instantaneousFlowRate);

            LocalDateTime first = firstTsByDay.get(day);
            LocalDateTime lastTs = lastTsByDay.get(day);

            long minutes = (first != null && lastTs != null)
                    ? Duration.between(first, lastTs).toMinutes()
                    : 0;

            BigDecimal dailyOperationHours = toHoursDecimal(minutes);
            BigDecimal maxDailyOperationHours = hoursDayByMonth.getOrDefault(ym.getMonthValue(), BigDecimal.ZERO);

            values.add(new DailyVolumeDTO(
                    day.toString(),
                    daily,
                    dailyOperationHours,
                    maxDailyOperationHours,
                    instantaneousFlowRate,
                    calculatedDailyMeasure
            ));

            prevLast = last;
        }

        return values;
    }

    private static BigDecimal normalizeValue(BigDecimal v) {
        return v != null ? v : BigDecimal.ZERO;
    }

    private BigDecimal resolveInstantaneousFlowRate(Long externalId, YearMonth ym) {
        LocalDateTime start = ym.atDay(1).atStartOfDay();
        LocalDateTime end   = ym.atEndOfMonth().atTime(LocalTime.MAX);

        return instantaneousFlowRateRepository
                .findEffectiveForPeriod(externalId, start, end)
                .map(InstantaneousFlowRate::getMeasurement)
                .or(() -> usageGrantRepository
                        .findFirstByExternalIdOrderByStartDateDesc(externalId)
                        .map(UsageGrant::getMaximumFlowRate))
                .orElse(BigDecimal.ZERO);
    }

    private static BigDecimal toHoursDecimal(long totalMinutes) {
        long minutes = Math.clamp(totalMinutes, 0L, 1440L);
        return BigDecimal.valueOf(minutes)
                .divide(BigDecimal.valueOf(60), 1, RoundingMode.HALF_UP);
    }
}
