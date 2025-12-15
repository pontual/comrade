package com.pontual_telemetria.pontual_monitor_api.domain.service;

import com.pontual_telemetria.pontual_monitor_api.domain.repository.OperationSummaryRepository;
import com.pontual_telemetria.pontual_monitor_api.domain.repository.UsageGrantRepository;
import com.pontual_telemetria.pontual_monitor_api.domain.repository.projection.OperationSummaryProjection;
import com.pontual_telemetria.pontual_monitor_api.infrastructure.util.ApplicationUtils;
import com.pontual_telemetria.pontual_monitor_api.web.dto.dashboard.OperationSummaryDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OperationSummaryDomainService {

    private final ApplicationUtils applicationUtils;
    private final OperationSummaryRepository repository;
    private final UsageGrantRepository usageGrantRepository;
    private final MaterializedViewRefreshDomainService mvRefresh;

    public List<OperationSummaryDTO> getOperationSummaryByLocationId(
            Long locationId,
            Integer year,
            boolean awaitFresh
    ) {

        if (awaitFresh) {
            mvRefresh.awaitIfRefreshing(locationId, 3000);
        }

        final int yearSearch = (year != null ? year : resolveYearFromLatestGrant(locationId));
        final boolean cap24On = applicationUtils.enableCapTo24();

        List<OperationSummaryProjection> summaries =
                repository.summaryByLocationId(locationId, yearSearch, cap24On);

        UsageGrantAnnualTotals annualGrant =
                resolveAnnualUsageGrant(locationId);

        return summaries.stream()
                .map(p -> toDTO(p, annualGrant))
                .toList();
    }

    private OperationSummaryDTO toDTO(
            OperationSummaryProjection p,
            UsageGrantAnnualTotals annualGrant
    ) {

        BigDecimal consumedVolume = p.getVolume_total_operation();
        BigDecimal grantedVolume  = annualGrant.totalVolume();
        BigDecimal grantedDuration = annualGrant.totalDuration();

        BigDecimal utilization =
                calculateUtilization(consumedVolume, grantedVolume);

        return new OperationSummaryDTO(
                p.getExternal_id(),
                p.getDuration_operation_hours(),
                grantedDuration,
                consumedVolume,
                grantedVolume,
                p.getAverage_flow(),
                p.getLast_read(),
                p.getMaximum_flow_rate(),
                utilization
        );
    }

    private UsageGrantAnnualTotals resolveAnnualUsageGrant(Long locationId) {

        return usageGrantRepository
                .findTopByLocationIdOrderByStartDateDesc(locationId)
                .map(ug -> new UsageGrantAnnualTotals(
                        defaultIfNull(ug.getTotalVolume()),
                        defaultIfNull(ug.getTotalDuration())
                ))
                .orElse(new UsageGrantAnnualTotals(
                        BigDecimal.ZERO,
                        BigDecimal.ZERO
                ));
    }

    private BigDecimal calculateUtilization(
            BigDecimal consumed,
            BigDecimal granted
    ) {
        if (consumed == null || granted == null) {
            return BigDecimal.ZERO;
        }
        if (BigDecimal.ZERO.compareTo(granted) == 0) {
            return BigDecimal.ZERO;
        }

        return consumed
                .divide(granted, 6, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    private BigDecimal defaultIfNull(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }


    private int resolveYearFromLatestGrant(Long locationId) {
        final int currentYear = LocalDate.now().getYear();

        return usageGrantRepository
                .findTopByLocationIdOrderByStartDateDesc(locationId)
                .map(ug -> {
                    LocalDate start = ug.getStartDate().toLocalDate();
                    LocalDate end   = endInclusive(ug.getEndDate());

                    int y = currentYear;
                    if (y < start.getYear()) y = start.getYear();
                    if (y > end.getYear())   y = end.getYear();
                    return y;
                })
                .orElse(currentYear);
    }

    private static LocalDate endInclusive(LocalDateTime endDateTime) {
        if (endDateTime == null) {
            return LocalDate.of(9999, 12, 31);
        }
        return endDateTime.toLocalDate().minusDays(1);
    }

    private record UsageGrantAnnualTotals(
            BigDecimal totalVolume,
            BigDecimal totalDuration
    ) {}
}
