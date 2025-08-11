package com.pontual_telemetria.pontual_monitor_api.domain.service;

import com.pontual_telemetria.pontual_monitor_api.application.mapper.UsageGrantMonthlyMapper;
import com.pontual_telemetria.pontual_monitor_api.domain.exception.PontualMonitorException;
import com.pontual_telemetria.pontual_monitor_api.domain.model.customer.Location;
import com.pontual_telemetria.pontual_monitor_api.domain.model.regulatory.UsageGrant;
import com.pontual_telemetria.pontual_monitor_api.domain.model.regulatory.UsageGrantMonthly;
import com.pontual_telemetria.pontual_monitor_api.domain.repository.LocationRepository;
import com.pontual_telemetria.pontual_monitor_api.domain.repository.UsageGrantMonthlyRepository;
import com.pontual_telemetria.pontual_monitor_api.domain.repository.UsageGrantRepository;
import com.pontual_telemetria.pontual_monitor_api.web.dto.regulatory.UsageGrantMonthlyDTO;
import com.pontual_telemetria.pontual_monitor_api.web.dto.regulatory.UsageGrantRequestDTO;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class UsageGrantDominaService {

    private final UsageGrantRepository usageGrantRepository;
    private final LocationRepository locationRepository;
    private final UsageGrantMonthlyRepository usageGrantMonthlyRepository;
    private final UsageGrantMonthlyMapper usageGrantMonthlyMapper;

    @Transactional
    public void create(UsageGrantRequestDTO usageGrantRequestDTO) {
        Location location = locationRepository.findByExternalId(usageGrantRequestDTO.getLocationId());

        if (location == null) {
            throw new EntityNotFoundException("Localização não encontrada");
        }

        UsageGrant usageGrant = UsageGrant.builder()
                .location(location)
                .identifier(usageGrantRequestDTO.getIdentifier())
                .startDate(usageGrantRequestDTO.getStartDate())
                .endDate(usageGrantRequestDTO.getEndDate())
                .totalDuration(usageGrantRequestDTO.getTotalDuration())
                .totalVolume(usageGrantRequestDTO.getTotalVolume())
                .maximumFlowRate(usageGrantRequestDTO.getMaximumFlowRate())
                .build();

        List<UsageGrantMonthly> monthlyList = new ArrayList<>();

        for (int month = 1; month <= 12; month++) {
            monthlyList.add(UsageGrantMonthly.builder()
                    .usageGrant(usageGrant)
                    .month(month)
                    .flowRate(null)
                    .hoursDay(null)
                    .daysMonth(null)
                    .maximumVolume(null)
                    .build());
        }

        usageGrant.setMonthlyGrants(monthlyList);
        usageGrantRepository.save(usageGrant);
    }

    @Transactional
    public void delete(Long id) {
        usageGrantRepository.delete(
                usageGrantRepository.findById(id)
                        .orElseThrow(() -> new PontualMonitorException(
                                "Outorga não encontrada",
                                "USAGE_GRANT_NOT_FOUND",
                                HttpStatus.BAD_REQUEST,
                                "Nenhum registro foi encontrado para a outorga informada.")
                        )
        );
    }

    @Transactional
    public void updateAllMonthly(List<UsageGrantMonthlyDTO> usageGrantMonthlyList) {
        List<UsageGrantMonthly> entity = usageGrantMonthlyMapper.toEntity(usageGrantMonthlyList);
        for (UsageGrantMonthly usageGrantMonthly : entity) {
            usageGrantMonthlyRepository.updateMonthly(
                    usageGrantMonthly.getId(),
                    usageGrantMonthly.getFlowRate(),
                    usageGrantMonthly.getHoursDay(),
                    usageGrantMonthly.getDaysMonth(),
                    usageGrantMonthly.getMaximumVolume()
            );
        }
    }
}
