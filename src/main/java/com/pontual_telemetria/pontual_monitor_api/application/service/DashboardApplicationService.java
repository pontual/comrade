package com.pontual_telemetria.pontual_monitor_api.application.service;

import com.pontual_telemetria.pontual_monitor_api.domain.service.DashboardDomainService;
import com.pontual_telemetria.pontual_monitor_api.web.dto.dashboard.InfoPanelDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardApplicationService {

    private final DashboardDomainService dashboardDomainService;

    public List<Integer> listAvailableYears(Long externalId) {
        log.info("[GET-DASHBOARD-AVAILABLE-YEARS] Recuperando listagem de anos para o Dashboard");
        List<Integer> availableYears = dashboardDomainService.listAvailableYears(externalId);
        log.info("[GET-DASHBOARD-AVAILABLE-YEARS] - Listagem de anos para o Dashboard retornada com sucesso");
        return availableYears;
    }

    public InfoPanelDTO dashboardInfoByYear(Long externalId, int year) {
        log.info("[GET-DASHBOARD-MONTHLY] - Recuperando informações para o Dashboard por ano");
        InfoPanelDTO infoPanelDTO = dashboardDomainService.dashboardInfoByYear(externalId, year);
        log.info("[GET-DASHBOARD-MONTHLY] - Informações para o Dashboard por ano retornadas com sucess");
        return infoPanelDTO;
    }

    public InfoPanelDTO dashboardInfo(Long locationId){
        log.info("[GET-DASHBOARD-INFO] Recuperando informações do Dashboard");
        InfoPanelDTO infoPanelDTO = dashboardDomainService.dashboardInfo(locationId);
        log.info("[GET-DASHBOARD-INFO] Informações do Dashboard retornadas com sucesso");
        return infoPanelDTO;
    }
}
