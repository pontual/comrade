package com.pontual_telemetria.pontual_monitor_api.application.service;

import com.pontual_telemetria.pontual_monitor_api.domain.service.LocationDomainService;
import com.pontual_telemetria.pontual_monitor_api.web.dto.sgman.asset.SgmanAssetDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocationApplicationService {

    private final LocationDomainService locationDomainService;

    public void updateLocationBySgman(List<SgmanAssetDTO> sgmanAssetDTO) {
        log.info("Iniciada a consulta de localizações cadastrados no SGMAN");
        locationDomainService.updateLocationBySgman(sgmanAssetDTO);
        log.info("Finalizada a consulta de localizações cadastrados no SGMAN");
    }
}
