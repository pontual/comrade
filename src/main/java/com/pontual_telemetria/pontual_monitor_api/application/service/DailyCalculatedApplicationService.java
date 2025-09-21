package com.pontual_telemetria.pontual_monitor_api.application.service;

import com.pontual_telemetria.pontual_monitor_api.web.dto.monitoring.dailyoperation.DailyCalculatedItemDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DailyCalculatedApplicationService {

    private final DailyCalculatedDomainService domain;

    public List<DailyCalculatedItemDTO> getCalculated(Long externalId) {
        log.info("[DAILY-CALCULATED] Recuperando informações de operação diária calculada para o patrimonio {}", externalId);
        List<DailyCalculatedItemDTO> values = domain.getCalculated(externalId);
        log.info("[DAILY-CALCULATED] Informações de operação diária calculada para o patrimonio {} retornadas com sucesso", externalId);
        return values;
    }
}
