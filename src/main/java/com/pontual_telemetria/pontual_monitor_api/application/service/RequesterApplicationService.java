package com.pontual_telemetria.pontual_monitor_api.application.service;

import com.pontual_telemetria.pontual_monitor_api.application.mapper.RequesterMapper;
import com.pontual_telemetria.pontual_monitor_api.domain.model.customer.Requester;
import com.pontual_telemetria.pontual_monitor_api.domain.repository.RequesterRepository;
import com.pontual_telemetria.pontual_monitor_api.domain.service.RequesterDomainService;
import com.pontual_telemetria.pontual_monitor_api.web.dto.RequesterDTO;
import com.pontual_telemetria.pontual_monitor_api.web.dto.sgman.requester.SgmanRequesterDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequesterApplicationService {

    private final RequesterDomainService requesterDomainService;
    private final RequesterRepository  requesterRepository;
    private final RequesterMapper requesterMapper;

    public  List<RequesterDTO> getAll() {
        log.info("Recuperando lista de solicitantes");
        List<Requester> requesters = requesterRepository.findAll();
        log.info("Lista de solicitantes recuperada");
        return requesterMapper.toDtoList(requesters);
    }

    public void updateRequestsBySgman(List<SgmanRequesterDTO> sgmanRequestersDTO) {
        log.info("Iniciada a consulta de solicitantes cadastrados no SGMAN");
        requesterDomainService.updateRequestsBySgman(sgmanRequestersDTO);
        log.info("Finalizada a consulta de solicitantes cadastrados no SGMAN");
    }
}
