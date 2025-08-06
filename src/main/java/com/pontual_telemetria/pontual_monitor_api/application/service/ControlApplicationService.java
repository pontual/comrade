package com.pontual_telemetria.pontual_monitor_api.application.service;

import com.pontual_telemetria.pontual_monitor_api.application.mapper.ControlMapper;
import com.pontual_telemetria.pontual_monitor_api.domain.repository.ControlRepository;
import com.pontual_telemetria.pontual_monitor_api.domain.service.ControlDomainService;
import com.pontual_telemetria.pontual_monitor_api.web.dto.control.ControlDTO;
import com.pontual_telemetria.pontual_monitor_api.web.dto.control.ControlReadingRequestDTO;
import com.pontual_telemetria.pontual_monitor_api.web.dto.control.ControlReadingResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ControlApplicationService {

    private final ControlDomainService controlDomainService;
    private final ControlRepository controlRepository;
    private final ControlMapper controlMapper;

    public List<ControlDTO> getControlsByExternalId(Long externalId) {
        log.info("[GET-CONTROLS] Recuperando lista de controles");
        List<ControlDTO> controls = controlMapper.toListDto(controlRepository.findAllWithReadings(externalId));
        log.info("[GET-CONTROLS] Lista de controles recuperada com sucesso");
        return controls;
    }

    public void create(ControlDTO controlDTO) {
        log.info("[CREATE-CONTROL] Criando novo controle");
        controlDomainService.create(controlDTO);
        log.info("[CREATE-CONTROL] Controle criado com sucesso");
    }

    public void disable(Long id) {
        log.info("[UPDATE-CONTROL] Desativando controle id={}", id);
        controlDomainService.disable(id);
        log.info("[UPDATE-CONTROL] Controle id={} desativado com sucesso", id);
    }

    public List<ControlReadingResponseDTO> getReadingDataByLocationId(Long locationId){
        log.info("[GET-READING-DATA] Recuperando registros de controle para o id{}", locationId);
        List<ControlReadingResponseDTO> list = controlDomainService.getReadingDataByLocationId(locationId);
        log.info("[GET-READING-DATA] Registros de controle retornados com sucesso para o id{}", locationId);
        return list;
    }

    public void createReadingData(List<ControlReadingRequestDTO> controlReadingRequestDTO) {
        log.info("[CREATE-READING-CONTROL] Registrando dados de controle");
        controlDomainService.createReadingData(controlReadingRequestDTO);
        log.info("[CREATE-READING-CONTROL] Dados de controle registrados com sucesso");
    }
}
