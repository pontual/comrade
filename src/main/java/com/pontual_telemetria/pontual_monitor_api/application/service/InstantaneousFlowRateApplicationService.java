package com.pontual_telemetria.pontual_monitor_api.application.service;

import com.pontual_telemetria.pontual_monitor_api.application.mapper.InstantaneousFlowRateMapper;
import com.pontual_telemetria.pontual_monitor_api.domain.repository.InstantaneousFlowRateRepository;
import com.pontual_telemetria.pontual_monitor_api.domain.service.InstantaneousFlowRateDomainService;
import com.pontual_telemetria.pontual_monitor_api.web.dto.monitoring.instantaneousflowtate.InstantaneousFlowRateRequestDTO;
import com.pontual_telemetria.pontual_monitor_api.web.dto.monitoring.instantaneousflowtate.InstantaneousFlowRateResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InstantaneousFlowRateApplicationService {

    private final InstantaneousFlowRateRepository instantaneousFlowRateRepository;
    private final InstantaneousFlowRateDomainService instantaneousFlowRateDomainService;
    private final InstantaneousFlowRateMapper mapper;

    public List<InstantaneousFlowRateResponseDTO> getAllByLocationId(Long locationId) {
        log.info("[GET-INSTANTANEOUS-FLOW-RATE] Recuperando lista de vazões instantâneas");
        var entities = instantaneousFlowRateRepository.findAllByLocation_Id(locationId);
        var response = mapper.toListDTO(entities);
        log.info("[GET-INSTANTANEOUS-FLOW-RATE] Lista de vazões instantâneas retornada com sucesso");
        return response;
    }

    public void create(InstantaneousFlowRateRequestDTO instantaneousFlowRateRequestDTO){
        log.info("[POST-INSTANTANEOUS-FLOW-RATE] Criando vazão instantânea");
        instantaneousFlowRateDomainService.create(instantaneousFlowRateRequestDTO);
        log.info("[POST-INSTANTANEOUS-FLOW-RATE] Vazão instantânea criada com sucesso");
    }

    public void delete(Long id) {
        log.info("[DELETE-INSTANTANEOUS-FLOW-RATE] Excluíndo vazão instantânea");
        instantaneousFlowRateDomainService.delete(id);
        log.info("[DELETE-INSTANTANEOUS-FLOW-RATE] Vazão instantânea excluída com sucesso");
    }
}
