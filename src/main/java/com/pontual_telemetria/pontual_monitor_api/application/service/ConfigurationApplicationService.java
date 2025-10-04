package com.pontual_telemetria.pontual_monitor_api.application.service;

import com.pontual_telemetria.pontual_monitor_api.application.mapper.ConfigurationMapper;
import com.pontual_telemetria.pontual_monitor_api.domain.service.ConfigurationDomainService;
import com.pontual_telemetria.pontual_monitor_api.web.dto.configuration.FunctionDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigurationApplicationService {

    private final ConfigurationDomainService domainService;
    private final ConfigurationMapper mapper;

    public List<FunctionDTO> functions() {
        log.info("[GET-FUNCTIONS] - Recuperando lista de funcionalidades cadastradas");
        List<FunctionDTO> functions = mapper.toDTO(domainService.functions());
        log.info("[GET-FUNCTIONS] - Funcionalidades retornadas com sucesso");
        return functions;
    }

    public void update(Long id, Boolean enabled) {
        log.info("[UPDATE-FUNCTION] - Atualizando status da funcionalidade id: {}", id);
        domainService.update(id, enabled);
        log.info("[GET-FUNCTIONS] - Status da funcionalidade id: {} atualizada com sucesso", id);
    }
}
