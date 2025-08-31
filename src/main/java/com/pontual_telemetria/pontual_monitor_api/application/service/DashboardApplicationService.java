package com.pontual_telemetria.pontual_monitor_api.application.service;

import com.pontual_telemetria.pontual_monitor_api.domain.service.DashboardDomainService;
import com.pontual_telemetria.pontual_monitor_api.web.dto.dashboard.InfoPanelDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardApplicationService {

    private final DashboardDomainService dashboardDomainService;

    public InfoPanelDTO dashboardInfo(Long locationId){
        log.info("[GET-DASHBOARD-INFO] Recuperando informações do Dashboard");
        InfoPanelDTO infoPanelDTO = dashboardDomainService.dashboardInfo(locationId);
        log.info("[GET-DASHBOARD-INFO] Informações do Dashboard retornadas com sucesso");
        return infoPanelDTO;
    }
}
