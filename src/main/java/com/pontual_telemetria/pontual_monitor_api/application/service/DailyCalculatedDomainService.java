package com.pontual_telemetria.pontual_monitor_api.application.service;

import com.pontual_telemetria.pontual_monitor_api.infrastructure.jdbc.DailyCalculatedJdbcRepository;
import com.pontual_telemetria.pontual_monitor_api.web.dto.monitoring.dailyoperation.DailyCalculatedItemDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DailyCalculatedDomainService {

    private final DailyCalculatedJdbcRepository repository;

    public List<DailyCalculatedItemDTO> getCalculated(Long externalId) {
        return repository.findAllByExternalId(externalId);
    }
}
