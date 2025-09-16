package com.pontual_telemetria.pontual_monitor_api.domain.service;

import com.pontual_telemetria.pontual_monitor_api.domain.repository.OperationSummaryRepository;
import com.pontual_telemetria.pontual_monitor_api.domain.repository.UsageGrantRepository;
import com.pontual_telemetria.pontual_monitor_api.domain.repository.projection.OperationSummaryProjection;
import com.pontual_telemetria.pontual_monitor_api.web.dto.dashboard.OperationSummaryDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OperationSummaryDomainService {

    private final OperationSummaryRepository repository;
    private final UsageGrantRepository usageGrantRepository;

    private static LocalDate endInclusive(LocalDateTime endDateTime) {
        if (endDateTime == null) return LocalDate.of(9999,12,31);
        return endDateTime.toLocalDate().minusDays(1);
    }

    public List<OperationSummaryDTO> getOperationSummaryByLocationId(Long locationId, Integer year) {
        final int yearSearch = (year != null ? year : resolveYearFromLatestGrant(locationId));

        List<OperationSummaryProjection> data = repository.summaryByLocationId(locationId, yearSearch);

        return data.stream().map(p -> new OperationSummaryDTO(
                p.getExternal_id(),
                p.getDuration_operation_hours(),
                p.getDuration_usage_grant_hours(),
                p.getVolume_total_operation(),
                p.getVolume_usage_grant(),
                p.getAverage_flow(),
                p.getLast_read(),
                p.getMaximum_flow_rate(),
                p.getUtilization()
        )).toList();
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
}
