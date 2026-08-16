package com.pontual_telemetria.pontual_monitor_api.domain.service;

import com.pontual_telemetria.pontual_monitor_api.application.service.DailyCalculatedDomainService;
import com.pontual_telemetria.pontual_monitor_api.domain.model.regulatory.UsageGrant;
import com.pontual_telemetria.pontual_monitor_api.domain.model.regulatory.UsageGrantMonthly;
import com.pontual_telemetria.pontual_monitor_api.domain.model.view.MvDailyAgg;
import com.pontual_telemetria.pontual_monitor_api.domain.repository.MVDailyAggRepository;
import com.pontual_telemetria.pontual_monitor_api.domain.repository.MVMonthlyAggRepository;
import com.pontual_telemetria.pontual_monitor_api.domain.repository.UsageGrantRepository;
import com.pontual_telemetria.pontual_monitor_api.infrastructure.util.ApplicationUtils;
import com.pontual_telemetria.pontual_monitor_api.web.dto.dashboard.DailyVolumeDTO;
import com.pontual_telemetria.pontual_monitor_api.web.dto.dashboard.InfoPanelDTO;
import com.pontual_telemetria.pontual_monitor_api.web.dto.dashboard.MonthlyVolumeDTO;
import com.pontual_telemetria.pontual_monitor_api.web.dto.dashboard.UsageGrantDashboardInfoDTO;
import com.pontual_telemetria.pontual_monitor_api.web.dto.monitoring.dailyoperation.DailyCalculatedItemDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardDomainService {

    private final ApplicationUtils applicationUtils;
    private final UsageGrantRepository usageGrantRepository;
    private final MVDailyAggRepository mvDailyAggRepository;
    private final MVMonthlyAggRepository mvMonthlyAggRepository;
    private final DailyCalculatedDomainService dailyCalculatedDomainService;

    private static final DateTimeFormatter MMYYYY = DateTimeFormatter.ofPattern("MM/yyyy");
    private static final BigDecimal H24 = new BigDecimal("24");

    public List<Integer> listAvailableYears(Long externalId) {
        return mvDailyAggRepository.findAvailableYears(externalId);
    }

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

    public List<MonthlyVolumeDTO> buildMonthlyVolumeByYear(Long externalId, int year) {
        var rows = mvMonthlyAggRepository.findMonthlyAggByYear(externalId, year);
        if (rows == null || rows.isEmpty()) return List.of();

        return rows.stream()
                .map(r -> {
                    BigDecimal monthlyVolume = normalizeValue(r.getMonthlyVolume());
                    BigDecimal monthlyOpHours = normalizeValue(r.getMonthlyOpHours());

                    BigDecimal capturedAvg = monthlyOpHours.signum() == 0
                            ? BigDecimal.ZERO.setScale(3, RoundingMode.HALF_UP)
                            : monthlyVolume.divide(monthlyOpHours, 3, RoundingMode.HALF_UP);

                    return new MonthlyVolumeDTO(
                            MMYYYY.format(YearMonth.from(r.getYm())),
                            r.getMonthlyVolume(),
                            capturedAvg,
                            null
                    );
                })
                .toList();
    }

    private Optional<UsageGrant> findLatestUsageGrant(Long externalId) {
        return usageGrantRepository
                .findAllByExternalIdOrderByStartDateDesc(externalId)
                .stream()
                .findFirst();
    }

    public List<DailyVolumeDTO> buildDailyVolumeByYear(Long externalId, int year) {
        var rows = mvDailyAggRepository.findDailyAggByYear(externalId, year);
        if (rows == null || rows.isEmpty()) return List.of();

        // mv_daily_agg computes calculatedDailyMeasure as daily_pulse_diff * inst_flow_rate,
        // which is wrong for API sources — the correct volume is calculated_volume sent by the API.
        // Build a lookup from vw_daily_calculated_final to override API-source days.
        Map<LocalDate, DailyCalculatedItemDTO> apiDayMap = dailyCalculatedDomainService
                .getCalculated(externalId).stream()
                .filter(d -> "API".equals(d.source()) && d.day().getYear() == year)
                .collect(Collectors.toMap(DailyCalculatedItemDTO::day, d -> d, (a, b) -> a));

        Map<Integer, BigDecimal> hoursDayByMonth = findLatestUsageGrant(externalId)
                .map(ug -> ug.getMonthlyGrants().stream()
                        .filter(Objects::nonNull)
                        .collect(Collectors.toMap(
                                UsageGrantMonthly::getMonth,
                                m -> Optional.ofNullable(m.getHoursDay()).orElse(BigDecimal.ZERO),
                                (a,b)->a)))
                .orElseGet(Collections::emptyMap);

        Map<Integer, BigDecimal> grantedFlowByMonth = resolveGrantedFlowByMonth(externalId);

        List<DailyVolumeDTO> out = new ArrayList<>(rows.size());
        for (var r : rows) {
            LocalDate day = r.getDay();
            BigDecimal maxDailyOpHours =
                    hoursDayByMonth.getOrDefault(YearMonth.from(day).getMonthValue(), BigDecimal.ZERO);

            DailyCalculatedItemDTO apiDay = apiDayMap.get(day);

            BigDecimal calculatedDailyMeasure;
            BigDecimal averageDailyFlowRate;
            if (apiDay != null) {
                calculatedDailyMeasure = apiDay.effVolume() == null
                        ? BigDecimal.ZERO.setScale(3, RoundingMode.HALF_UP)
                        : apiDay.effVolume().setScale(3, RoundingMode.HALF_UP);
                averageDailyFlowRate = r.getInstFlowRate() == null
                        ? BigDecimal.ZERO.setScale(3, RoundingMode.HALF_UP)
                        : r.getInstFlowRate().setScale(3, RoundingMode.HALF_UP);
            } else {
                calculatedDailyMeasure = r.getCalculatedDailyMeasure() == null
                        ? BigDecimal.ZERO.setScale(3, RoundingMode.HALF_UP)
                        : r.getCalculatedDailyMeasure().setScale(3, RoundingMode.HALF_UP);
                averageDailyFlowRate = null;
            }

            BigDecimal grantedAvgFlowRate = grantedFlowByMonth
                    .getOrDefault(YearMonth.from(day).getMonthValue(), BigDecimal.ZERO)
                    .setScale(3, RoundingMode.HALF_UP);

            out.add(new DailyVolumeDTO(
                    day.toString(),
                    r.getDailyPulseDiff() == null ? BigDecimal.ZERO.setScale(3, RoundingMode.HALF_UP) : r.getDailyPulseDiff().setScale(3, RoundingMode.HALF_UP),
                    r.getDailyOpHours()   == null ? BigDecimal.ZERO.setScale(3, RoundingMode.HALF_UP) : r.getDailyOpHours().setScale(3, RoundingMode.HALF_UP),
                    maxDailyOpHours       == null ? BigDecimal.ZERO.setScale(3, RoundingMode.HALF_UP) : maxDailyOpHours.setScale(3, RoundingMode.HALF_UP),
                    r.getInstFlowRate()   == null ? BigDecimal.ZERO.setScale(3, RoundingMode.HALF_UP) : r.getInstFlowRate().setScale(3, RoundingMode.HALF_UP),
                    calculatedDailyMeasure,
                    averageDailyFlowRate,
                    grantedAvgFlowRate
            ));
        }
        return out;
    }

    public InfoPanelDTO dashboardInfoByYear(Long externalId, int year) {
        var dto = new InfoPanelDTO();
        dto.setLocationId(externalId);

        var monthly = buildMonthlyVolumeByYear(externalId, year);
        var daily   = buildDailyVolumeByYear(externalId, year);

        dto.setMonthlyVolumeList(monthly);
        dto.setDailyVolumeList(daily);
        dto.setUsageGrantDashboardInfo(
                buildUsageGrantVolumeInfo(externalId, daily)
        );
        return dto;
    }

    private List<MonthlyVolumeDTO> buildMonthlyVolume(Long externalId) {
        List<DailyVolumeDTO> daily = buildDailyVolume(externalId);
        if (daily.isEmpty()) return List.of();

        Map<Integer, BigDecimal> grantedFlowByMonth = resolveGrantedFlowByMonth(externalId);

        Map<YearMonth, BigDecimal> volumeByMonth = new TreeMap<>();
        Map<YearMonth, BigDecimal> hoursByMonth = new TreeMap<>();

        for (DailyVolumeDTO d : daily) {
            if (d == null) continue;
            YearMonth ym = YearMonth.from(LocalDate.parse(d.getDay()));

            volumeByMonth.merge(ym, normalizeValue(d.getCalculatedDailyMeasure()), BigDecimal::add);
            hoursByMonth.merge(ym, normalizeValue(d.getDailyOperationHours()), BigDecimal::add);
        }

        return volumeByMonth.entrySet().stream()
                .map(e -> {
                    YearMonth ym = e.getKey();
                    BigDecimal monthHours = hoursByMonth.getOrDefault(ym, BigDecimal.ZERO);

                    BigDecimal capturedAvg = monthHours.signum() == 0
                            ? BigDecimal.ZERO.setScale(3, RoundingMode.HALF_UP)
                            : e.getValue().divide(monthHours, 3, RoundingMode.HALF_UP);

                    BigDecimal grantedAvg = grantedFlowByMonth
                            .getOrDefault(ym.getMonthValue(), BigDecimal.ZERO)
                            .setScale(3, RoundingMode.HALF_UP);

                    return new MonthlyVolumeDTO(
                            ym.format(MMYYYY),
                            e.getValue(),
                            capturedAvg,
                            grantedAvg
                    );
                })
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

        return findLatestUsageGrant(externalId)
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
                            .reduce(BigDecimal.ZERO, BigDecimal::add)
                            .setScale(3, RoundingMode.HALF_UP);

                    BigDecimal totalVolume = months.stream()
                            .map(m -> normalizeValue(m.getMaximumVolume()))
                            .reduce(BigDecimal.ZERO, BigDecimal::add)
                            .setScale(3, RoundingMode.HALF_UP);

                    BigDecimal averageFlow = totalDuration.signum() == 0
                            ? BigDecimal.ZERO
                            : totalVolume.divide(totalDuration, 6, RoundingMode.HALF_EVEN);

                    return months.stream()
                            .map(m -> {
                                int calendarMonth = Optional.ofNullable(m.getMonth()).orElse(1);

                                YearMonth ymResolved = opHoursByMonth.keySet().stream()
                                        .filter(k -> k != null && k.getMonthValue() == calendarMonth)
                                        .findFirst()
                                        .orElse(YearMonth.of(startYm.getYear(), calendarMonth));

                                BigDecimal monthlyOperationHours = opHoursByMonth
                                        .getOrDefault(ymResolved, BigDecimal.ZERO)
                                        .setScale(3, RoundingMode.HALF_UP);

                                BigDecimal monthlyUsageGrantVolume = normalizeValue(m.getMaximumVolume()).setScale(3, RoundingMode.HALF_UP);
                                BigDecimal hoursDay  = normalizeValue(m.getHoursDay()).setScale(3, RoundingMode.HALF_UP);
                                BigDecimal daysMonth = normalizeValue(m.getDaysMonth()).setScale(3, RoundingMode.HALF_UP);
                                BigDecimal monthDuration = hoursDay.multiply(daysMonth).setScale(3, RoundingMode.HALF_UP);
                                BigDecimal maxMonthlyOperationHours = monthDuration.setScale(3, RoundingMode.HALF_UP);

                                return new UsageGrantDashboardInfoDTO(
                                        calendarMonth,
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
        List<DailyCalculatedItemDTO> rows = dailyCalculatedDomainService.getCalculated(externalId);
        if (rows == null || rows.isEmpty()) return List.of();

        // vw_daily_calculated_final computes mv_daily_hours as the intra-day delta,
        // which is always 0 for API sources that send a single reading at midnight.
        // daily_pulse_diff from mv_daily_agg uses a LAG across days and is the correct measure.
        Map<LocalDate, BigDecimal> pulseDiffByDay = mvDailyAggRepository
                .findAllById_ExternalId(externalId).stream()
                .collect(Collectors.toMap(m -> m.getId().getDay(), MvDailyAgg::getDailyPulseDiff, (a, b) -> a));

        Map<Integer, BigDecimal> hoursDayByMonth = findLatestUsageGrant(externalId)
                .map(ug -> ug.getMonthlyGrants().stream()
                        .collect(Collectors.toMap(
                                UsageGrantMonthly::getMonth,
                                m -> Optional.ofNullable(m.getHoursDay()).orElse(BigDecimal.ZERO),
                                (a, b) -> a)))
                .orElseGet(Collections::emptyMap);

        Map<Integer, BigDecimal> grantedFlowByMonth = resolveGrantedFlowByMonth(externalId);

        boolean cap24On = applicationUtils.enableCapTo24();

        List<DailyVolumeDTO> out = new ArrayList<>(rows.size());
        for (DailyCalculatedItemDTO r : rows) {
            LocalDate day = r.day();
            YearMonth ym = YearMonth.from(day);

            BigDecimal maxDailyOpHours =
                    hoursDayByMonth.getOrDefault(ym.getMonthValue(), BigDecimal.ZERO);

            BigDecimal effDailyHoursRaw = r.effDailyHours();
            BigDecimal effDailyHours = cap24On
                    ? capTo24(effDailyHoursRaw)
                    : (effDailyHoursRaw == null
                    ? BigDecimal.ZERO.setScale(3, RoundingMode.HALF_UP)
                    : effDailyHoursRaw.setScale(3, RoundingMode.HALF_UP));

            BigDecimal effVolume = r.effVolume() == null
                    ? BigDecimal.ZERO.setScale(3, RoundingMode.HALF_UP)
                    : r.effVolume().setScale(3, RoundingMode.HALF_UP);

            BigDecimal dailyMeasure;
            if ("API".equals(r.source())) {
                BigDecimal pulseDiff = pulseDiffByDay.get(day);
                dailyMeasure = pulseDiff == null
                        ? BigDecimal.ZERO.setScale(3, RoundingMode.HALF_UP)
                        : pulseDiff.setScale(3, RoundingMode.HALF_UP);
            } else {
                dailyMeasure = r.mvDailyHours() == null
                        ? BigDecimal.ZERO.setScale(3, RoundingMode.HALF_UP)
                        : r.mvDailyHours().setScale(3, RoundingMode.HALF_UP);
            }

            BigDecimal averageDailyFlowRate = getAverageDailyFlowRate(r, effDailyHours, effVolume);

            BigDecimal grantedAvgFlowRate = grantedFlowByMonth
                    .getOrDefault(ym.getMonthValue(), BigDecimal.ZERO)
                    .setScale(3, RoundingMode.HALF_UP);


            out.add(new DailyVolumeDTO(
                    day.toString(),
                    dailyMeasure,
                    effDailyHours,
                    maxDailyOpHours == null ? BigDecimal.ZERO.setScale(3, RoundingMode.HALF_UP) : maxDailyOpHours.setScale(3, RoundingMode.HALF_UP),
                    r.instFlowRate() == null ? BigDecimal.ZERO.setScale(3, RoundingMode.HALF_UP) : r.instFlowRate().setScale(3, RoundingMode.HALF_UP),
                    effVolume,
                    averageDailyFlowRate,
                    grantedAvgFlowRate
            ));
        }
        return out;
    }

    private static BigDecimal getAverageDailyFlowRate(DailyCalculatedItemDTO r, BigDecimal effDailyHours, BigDecimal effVolume) {
        if ("API".equals(r.source())) {
            if (effDailyHours.signum() == 0) {
                // API sources with a single midnight reading have no intra-day horímetro delta,
                // so hours are always 0. Fall back to instFlowRate as the reference rate.
                return r.instFlowRate() == null
                        ? BigDecimal.ZERO.setScale(3, RoundingMode.HALF_UP)
                        : r.instFlowRate().setScale(3, RoundingMode.HALF_UP);
            }
            return effVolume.divide(effDailyHours, 3, RoundingMode.HALF_UP);
        }
        return r.instFlowRate() == null
                ? BigDecimal.ZERO.setScale(3, RoundingMode.HALF_UP)
                : r.instFlowRate().setScale(3, RoundingMode.HALF_UP);
    }

    private static BigDecimal normalizeValue(BigDecimal v) {
        return v != null ? v : BigDecimal.ZERO;
    }

    private static BigDecimal capTo24(BigDecimal hours) {
        if (hours == null) return BigDecimal.ZERO.setScale(3, RoundingMode.HALF_UP);
        return hours
                .max(BigDecimal.ZERO)
                .min(H24)
                .setScale(3, RoundingMode.HALF_UP);
    }

    private Map<Integer, BigDecimal> resolveGrantedFlowByMonth(Long externalId) {
        return findLatestUsageGrant(externalId)
                .map(ug -> ug.getMonthlyGrants().stream()
                        .filter(Objects::nonNull)
                        .collect(Collectors.toMap(
                                UsageGrantMonthly::getMonth,
                                m -> {
                                    BigDecimal duration = normalizeValue(m.getHoursDay())
                                            .multiply(normalizeValue(m.getDaysMonth()));
                                    return duration.signum() == 0
                                            ? BigDecimal.ZERO
                                            : normalizeValue(m.getMaximumVolume())
                                            .divide(duration, 6, RoundingMode.HALF_EVEN);
                                },
                                (a, b) -> a)))
                .orElseGet(Collections::emptyMap);
    }
}
