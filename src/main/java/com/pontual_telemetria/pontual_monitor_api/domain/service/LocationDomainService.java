package com.pontual_telemetria.pontual_monitor_api.domain.service;

import com.pontual_telemetria.pontual_monitor_api.application.mapper.SgmanToLocationMapper;
import com.pontual_telemetria.pontual_monitor_api.domain.model.customer.Location;
import com.pontual_telemetria.pontual_monitor_api.domain.repository.LocationRepository;
import com.pontual_telemetria.pontual_monitor_api.web.dto.sgman.asset.SgmanAssetDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationDomainService {

    private final LocationRepository locationRepository;
    private final SgmanToLocationMapper sgmanToLocationMapper;

    public void updateLocationBySgman(List<SgmanAssetDTO> sgmanAssetDTO) {
        for(SgmanAssetDTO assetDTO : sgmanAssetDTO) {
            boolean exists = locationRepository.existsByExternalId(assetDTO.getId());

            if(!exists) {
                Location location = sgmanToLocationMapper.toEntity(assetDTO);
                locationRepository.save(location);
            }
        }

    }
}
