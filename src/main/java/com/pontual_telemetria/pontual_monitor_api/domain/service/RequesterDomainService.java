package com.pontual_telemetria.pontual_monitor_api.domain.service;

import com.pontual_telemetria.pontual_monitor_api.application.mapper.SgmanToRequesterMapper;
import com.pontual_telemetria.pontual_monitor_api.domain.model.customer.Requester;
import com.pontual_telemetria.pontual_monitor_api.domain.repository.RequesterRepository;
import com.pontual_telemetria.pontual_monitor_api.web.dto.sgman.requester.SgmanRequesterDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RequesterDomainService {

    private final RequesterRepository requesterRepository;
    private final SgmanToRequesterMapper sgmanToRequesterMapper;

    @Transactional
    public void updateRequestsBySgman(List<SgmanRequesterDTO> sgmanRequesters) {
        for (SgmanRequesterDTO sgmanRequesterDTO : sgmanRequesters) {
            boolean exists = requesterRepository.existsByExternalId(sgmanRequesterDTO.getId());

            if(!exists) {
                Requester requester = sgmanToRequesterMapper.toEntity(sgmanRequesterDTO);
                requesterRepository.save(requester);
            }
        }
    }
}
