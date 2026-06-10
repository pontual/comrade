package com.pontual_telemetria.pontual_monitor_api.domain.service;

import com.pontual_telemetria.pontual_monitor_api.application.service.DailyCalculatedDomainService;
import com.pontual_telemetria.pontual_monitor_api.domain.model.regulatory.UsageGrant;
import com.pontual_telemetria.pontual_monitor_api.domain.model.regulatory.UsageGrantMonthly;
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
                .map(r -> new MonthlyVolumeDTO(
                        MMYYYY.format(YearMonth.from(r.getYm())),
                        r.getMonthlyVolume(),
                        null,
                        null
                ))
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

        Map<Integer, BigDecimal> hoursDayByMonth = findLatestUsageGrant(externalId)
                .map(ug -> ug.getMonthlyGrants().stream()
                        .filter(Objects::nonNull)
                        .collect(Collectors.toMap(
                                UsageGrantMonthly::getMonth,
                                m -> Optional.ofNullable(m.getHoursDay()).orElse(BigDecimal.ZERO),
                                (a,b)->a)))
                .orElseGet(Collections::emptyMap);

        List<DailyVolumeDTO> out = new ArrayList<>(rows.size());
        for (var r : rows) {
            LocalDate day = r.getDay();
            BigDecimal maxDailyOpHours =
                    hoursDayByMonth.getOrDefault(YearMonth.from(day).getMonthValue(), BigDecimal.ZERO);

            out.add(new DailyVolumeDTO(
                    day.toString(),
                    r.getDailyPulseDiff()         == null ? BigDecimal.ZERO.setScale(3, RoundingMode.HALF_UP) : r.getDailyPulseDiff().setScale(3, RoundingMode.HALF_UP),
                    r.getDailyOpHours()           == null ? BigDecimal.ZERO.setScale(3, RoundingMode.HALF_UP) : r.getDailyOpHours().setScale(3, RoundingMode.HALF_UP),
                    maxDailyOpHours               == null ? BigDecimal.ZERO.setScale(3, RoundingMode.HALF_UP) : maxDailyOpHours.setScale(3, RoundingMode.HALF_UP),
                    r.getInstFlowRate()           == null ? BigDecimal.ZERO.setScale(3, RoundingMode.HALF_UP) : r.getInstFlowRate().setScale(3, RoundingMode.HALF_UP),
                    r.getCalculatedDailyMeasure() == null ? BigDecimal.ZERO.setScale(3, RoundingMode.HALF_UP) : r.getCalculatedDailyMeasure().setScale(3, RoundingMode.HALF_UP),
                    null
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

        BigDecimal grantedAvgFlow = calcGrantedAvgFlow(externalId)
                .setScale(3, RoundingMode.HALF_UP);

        Map<YearMonth, BigDecimal> volumeByMonth = new TreeMap<>();
        Map<YearMonth, BigDecimal> flowSumByMonth = new TreeMap<>();
        Map<YearMonth, Integer> flowCountByMonth = new TreeMap<>();

        for (DailyVolumeDTO d : daily) {
            if (d == null) continue;
            YearMonth ym = YearMonth.from(LocalDate.parse(d.getDay()));

            volumeByMonth.merge(ym, normalizeValue(d.getCalculatedDailyMeasure()), BigDecimal::add);
            flowSumByMonth.merge(ym, normalizeValue(d.getAverageDailyFlowRate()), BigDecimal::add);
            flowCountByMonth.merge(ym, 1, Integer::sum);
        }

        return volumeByMonth.entrySet().stream()
                .map(e -> {
                    YearMonth ym = e.getKey();
                    int days = flowCountByMonth.getOrDefault(ym, 0);

                    BigDecimal capturedAvg = days == 0
                            ? BigDecimal.ZERO.setScale(3, RoundingMode.HALF_UP)
                            : flowSumByMonth.getOrDefault(ym, BigDecimal.ZERO)
                            .divide(BigDecimal.valueOf(days), 3, RoundingMode.HALF_UP);

                    return new MonthlyVolumeDTO(
                            ym.format(MMYYYY),
                            e.getValue(),
                            capturedAvg,
                            grantedAvgFlow
                    );
                })
                .toList();
    }

    private BigDecimal calcGrantedAvgFlow(Long externalId) {
        return findLatestUsageGrant(externalId)
                .map(ug -> {
                    List<UsageGrantMonthly> months = ug.getMonthlyGrants().stream()
                            .filter(Objects::nonNull)
                            .toList();

                    BigDecimal totalDuration = months.stream()
                            .map(m -> normalizeValue(m.getHoursDay()).multiply(normalizeValue(m.getDaysMonth())))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    BigDecimal totalVolume = months.stream()
                            .map(m -> normalizeValue(m.getMaximumVolume()))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    return totalDuration.signum() == 0
                            ? BigDecimal.ZERO
                            : totalVolume.divide(totalDuration, 6, RoundingMode.HALF_EVEN);
                })
                .orElse(BigDecimal.ZERO);
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

        Map<Integer, BigDecimal> hoursDayByMonth = findLatestUsageGrant(externalId)
                .map(ug -> ug.getMonthlyGrants().stream()
                        .collect(Collectors.toMap(
                                UsageGrantMonthly::getMonth,
                                m -> Optional.ofNullable(m.getHoursDay()).orElse(BigDecimal.ZERO),
                                (a, b) -> a)))
                .orElseGet(Collections::emptyMap);

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

            BigDecimal averageDailyFlowRate = getAverageDailyFlowRate(r, effDailyHours, effVolume);

            out.add(new DailyVolumeDTO(
                    day.toString(),
                    r.mvDailyHours() == null ? BigDecimal.ZERO.setScale(3, RoundingMode.HALF_UP) : r.mvDailyHours().setScale(3, RoundingMode.HALF_UP),
                    effDailyHours,
                    maxDailyOpHours == null ? BigDecimal.ZERO.setScale(3, RoundingMode.HALF_UP) : maxDailyOpHours.setScale(3, RoundingMode.HALF_UP),
                    r.instFlowRate() == null ? BigDecimal.ZERO.setScale(3, RoundingMode.HALF_UP) : r.instFlowRate().setScale(3, RoundingMode.HALF_UP),
                    effVolume,
                    averageDailyFlowRate
            ));
        }
        return out;
    }

    private static BigDecimal getAverageDailyFlowRate(DailyCalculatedItemDTO r, BigDecimal effDailyHours, BigDecimal effVolume) {
        if ("API".equals(r.source())) {
            return effDailyHours.signum() == 0
                    ? BigDecimal.ZERO.setScale(3, RoundingMode.HALF_UP)
                    : effVolume.divide(effDailyHours, 3, RoundingMode.HALF_UP);
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
    
}
