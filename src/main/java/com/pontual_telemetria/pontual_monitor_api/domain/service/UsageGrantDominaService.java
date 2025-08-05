package com.pontual_telemetria.pontual_monitor_api.domain.service;

import com.pontual_telemetria.pontual_monitor_api.domain.exception.PontualMonitorException;
import com.pontual_telemetria.pontual_monitor_api.domain.model.customer.Location;
import com.pontual_telemetria.pontual_monitor_api.domain.model.regulatory.UsageGrant;
import com.pontual_telemetria.pontual_monitor_api.domain.model.regulatory.UsageGrantMonthly;
import com.pontual_telemetria.pontual_monitor_api.domain.repository.LocationRepository;
import com.pontual_telemetria.pontual_monitor_api.domain.repository.UsageGrantMonthlyRepository;
import com.pontual_telemetria.pontual_monitor_api.domain.repository.UsageGrantRepository;
import com.pontual_telemetria.pontual_monitor_api.web.dto.regulatory.UsageGrantRequestDTO;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class UsageGrantDominaService {

    private final UsageGrantRepository usageGrantRepository;
    private final UsageGrantMonthlyRepository usageGrantMonthlyRepository;
    private final LocationRepository locationRepository;

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

        final BigDecimal zero = BigDecimal.ZERO;

        YearMonth current = YearMonth.from(usageGrantRequestDTO.getStartDate());
        YearMonth end = YearMonth.from(usageGrantRequestDTO.getEndDate());

        while (!current.isAfter(end)) {
            monthlyList.add(UsageGrantMonthly.builder()
                    .usageGrant(usageGrant)
                    .year(current.getYear())
                    .month(current.getMonthValue())
                    .flowRate(zero)
                    .hoursDay(zero)
                    .daysMonth(zero)
                    .maximumVolume(zero)
                    .build());

            current = current.plusMonths(1);
        }

        usageGrant.setMonthlyGrants(monthlyList);
        usageGrantRepository.save(usageGrant);
    }

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
}
