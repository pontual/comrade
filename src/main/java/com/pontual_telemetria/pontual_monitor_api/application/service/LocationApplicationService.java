package com.pontual_telemetria.pontual_monitor_api.application.service;

import com.pontual_telemetria.pontual_monitor_api.application.mapper.LocationMapper;
import com.pontual_telemetria.pontual_monitor_api.domain.model.customer.Location;
import com.pontual_telemetria.pontual_monitor_api.domain.repository.LocationRepository;
import com.pontual_telemetria.pontual_monitor_api.domain.service.LocationDomainService;
import com.pontual_telemetria.pontual_monitor_api.web.dto.location.LocationDTO;
import com.pontual_telemetria.pontual_monitor_api.web.dto.sgman.location.SgmanLocationDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocationApplicationService {

    private final LocationDomainService locationDomainService;
    private final LocationRepository locationRepository;
    private final LocationMapper locationMapper;

    public List<LocationDTO> getByRequesterId(Long requesterId){
        List<Location> locations = locationRepository.findAllByRequesterId(requesterId);
        return locationMapper.toListDto(locations);
    }

    public void updateLocationBySgman(List<SgmanLocationDTO> sgmanLocationDTO) {
        log.info("[UPDATE-LOCATION-SGMAN] Iniciada a consulta de localizações cadastrados no SGMAN");
        locationDomainService.updateLocationBySgman(sgmanLocationDTO);
        log.info("[UPDATE-LOCATION-SGMAN] Finalizada a consulta de localizações cadastrados no SGMAN");
    }
}
