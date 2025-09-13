package com.pontual_telemetria.pontual_monitor_api.domain.service;

import com.pontual_telemetria.pontual_monitor_api.domain.repository.OperationSummaryRepository;
import com.pontual_telemetria.pontual_monitor_api.domain.repository.projection.OperationSummaryProjection;
import com.pontual_telemetria.pontual_monitor_api.web.dto.dashboard.OperationSummaryDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OperationSummaryDomainService {

    private final OperationSummaryRepository repository;

    public List<OperationSummaryDTO> getOperationSummaryByLocationId(Long locationId, Integer year) {
        final int yearSearch = (year != null ? year : LocalDate.now().getYear());

        List<OperationSummaryProjection> data = repository.summaryByLocationId(locationId, yearSearch);

        return data.stream().map(p -> new OperationSummaryDTO(
                p.getExternal_id(),
                p.getDuration_operation_hours(),
                p.getDuration_usage_grant_hours(),
                p.getVolume_total_operation(),
                p.getVolume_usage_grant(),
                p.getAverage_flow(),
                p.getLast_read(),
                p.getUtilization()
        )).toList();
    }
}
