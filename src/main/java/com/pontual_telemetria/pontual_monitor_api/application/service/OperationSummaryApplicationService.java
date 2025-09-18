package com.pontual_telemetria.pontual_monitor_api.application.service;

import com.pontual_telemetria.pontual_monitor_api.domain.service.OperationSummaryDomainService;
import com.pontual_telemetria.pontual_monitor_api.web.dto.dashboard.OperationSummaryDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OperationSummaryApplicationService {

    private final OperationSummaryDomainService operationSummaryDomainService;

    public List<OperationSummaryDTO> getOperationSummaryByLocationId(Long locationId, Integer year, boolean awaitFresh) {
        log.info("[OPERATION-SUMMARY] - Recuperando informações de resumo de operações para a localização: {}", locationId);
        List<OperationSummaryDTO> operationSummary = operationSummaryDomainService.getOperationSummaryByLocationId(locationId, year, awaitFresh);
        log.info("[OPERATION-SUMMARY] - Informações de resumo de operações para a localização: {} retornadas com sucesso", locationId);
        return operationSummary;
    }
}
