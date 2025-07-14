package com.pontual_telemetria.pontual_monitor_api.domain.service;

import com.pontual_telemetria.pontual_monitor_api.application.mapper.SgmanToLocationMapper;
import com.pontual_telemetria.pontual_monitor_api.domain.model.customer.Location;
import com.pontual_telemetria.pontual_monitor_api.domain.repository.LocationRepository;
import com.pontual_telemetria.pontual_monitor_api.web.dto.sgman.location.SgmanLocationDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationDomainService {

    private final LocationRepository locationRepository;
    private final SgmanToLocationMapper sgmanToLocationMapper;

    public void updateLocationBySgman(List<SgmanLocationDTO> sgmanLocationDTO) {
        for(SgmanLocationDTO locationDTO : sgmanLocationDTO) {
            boolean exists = locationRepository.existsByExternalId(locationDTO.getId());

            if(!exists) {
                Location location = sgmanToLocationMapper.toEntity(locationDTO);
                locationRepository.save(location);
            }
        }

    }
}
