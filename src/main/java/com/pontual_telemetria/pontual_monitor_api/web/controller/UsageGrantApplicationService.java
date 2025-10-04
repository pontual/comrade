package com.pontual_telemetria.pontual_monitor_api.web.controller;

import com.pontual_telemetria.pontual_monitor_api.application.mapper.UsageGrantMapper;
import com.pontual_telemetria.pontual_monitor_api.domain.model.regulatory.UsageGrant;
import com.pontual_telemetria.pontual_monitor_api.domain.repository.UsageGrantRepository;
import com.pontual_telemetria.pontual_monitor_api.domain.service.UsageGrantDomainService;
import com.pontual_telemetria.pontual_monitor_api.web.dto.regulatory.UsageGrantDTO;
import com.pontual_telemetria.pontual_monitor_api.web.dto.regulatory.UsageGrantMonthlyDTO;
import com.pontual_telemetria.pontual_monitor_api.web.dto.regulatory.UsageGrantRequestDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UsageGrantApplicationService {

    private final UsageGrantDomainService usageGrantDomainService;
    private final UsageGrantRepository usageGrantRepository;
    private final UsageGrantMapper usageGrantMapper;

    public List<UsageGrantDTO> getAllByLocationId(Long locationId){
        log.info("[GET-USAGE-GRANT] Recuperando lista de outorgas");
        List<UsageGrant> usageGrantList = usageGrantRepository.getAllByLocationId(locationId);
        log.info("[GET-USAGE-GRANT] Lista de outorgas retornada com sucesso");
        return usageGrantMapper.toListDto(usageGrantList);
    }

    public void create(UsageGrantRequestDTO usageGrantRequestDTO) {
        log.info("[CREATE-USAGE-GRANT] Cadastrando outorga anual");
        usageGrantDomainService.create(usageGrantRequestDTO);
        log.info("[CREATE-USAGE-GRANT] Outorga anual cadastrada com sucesso");
    }

    public void delete(Long id) {
        log.info("[DELETE-USAGE-GRANT] Iniciada a exclusão da outorga anual id={}", id);
        usageGrantDomainService.delete(id);
        log.info("[DELETE-USAGE-GRANT] Outorga anual id={} apagada com sucesso", id);
    }

    @Transactional
    public void updateAllMonthly(List<UsageGrantMonthlyDTO> usageGrantMonthlyDTO) {
        log.info("[UPDATE-USAGE-GRANT] Iniciada a atualização dos dados mensais da outorga");
        usageGrantDomainService.updateAllMonthly(usageGrantMonthlyDTO);
        log.info("[UPDATE-USAGE-GRANT] Finalizada a atualização dos dados mensais da outorga");
    }
}
