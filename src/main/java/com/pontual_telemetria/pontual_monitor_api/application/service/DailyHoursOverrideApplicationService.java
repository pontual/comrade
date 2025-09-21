package com.pontual_telemetria.pontual_monitor_api.application.service;

import com.pontual_telemetria.pontual_monitor_api.domain.model.monitoring.DailyOperationHoursOverride;
import com.pontual_telemetria.pontual_monitor_api.domain.service.DailyHoursOverrideDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class DailyHoursOverrideApplicationService {

    private final DailyHoursOverrideDomainService dailyHoursOverrideDomainService;

    public DailyOperationHoursOverride upsert(Long externalId, LocalDate day, BigDecimal hours, String user) {
        log.info("[UPSERT-OPERATION-HOURS] Iniciada a atualização de dados diários");
        DailyOperationHoursOverride data = dailyHoursOverrideDomainService.upsert(externalId, day, hours, user);
        log.info("[UPSERT-OPERATION-HOURS] Finalizada a atualização de dados diários");
        return data;
    }

    public void delete(Long externalId, LocalDate day) {
        log.info("[DELETE-OPERATION-HOURS] Deletando registro de operação diária modificada");
        dailyHoursOverrideDomainService.delete(externalId, day);
        log.info("[DELETE-OPERATION-HOURS] Registro de operação diária modificada excluído com sucesso");
    }
}
