package com.pontual_telemetria.pontual_monitor_api.domain.service;

import com.pontual_telemetria.pontual_monitor_api.domain.exception.PontualMonitorException;
import com.pontual_telemetria.pontual_monitor_api.domain.model.configuration.Function;
import com.pontual_telemetria.pontual_monitor_api.domain.repository.ConfigurationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConfigurationDomainService {

    private final ConfigurationRepository repository;

    public List<Function> functions() {
        return repository.findAll();
    }

    @Transactional
    public void update(Long id, Boolean enabled) {
        Function function = repository.findById(id)
                .orElseThrow(() -> new PontualMonitorException(
                        "Funcionalidade não encontrada",
                        "FUNCTION_NOT_FOUND",
                        HttpStatus.NOT_FOUND,
                        "A funcionalidade solicitada não foi encontrada"
                ));

        function.setEnabled(enabled);
        repository.save(function);
    }
}
