package com.pontual_telemetria.pontual_monitor_api.domain.service;

import com.pontual_telemetria.pontual_monitor_api.domain.event.RefreshNeededEvent;
import com.pontual_telemetria.pontual_monitor_api.domain.exception.PontualMonitorException;
import com.pontual_telemetria.pontual_monitor_api.domain.model.customer.Location;
import com.pontual_telemetria.pontual_monitor_api.domain.model.monitoring.InstantaneousFlowRate;
import com.pontual_telemetria.pontual_monitor_api.domain.repository.InstantaneousFlowRateRepository;
import com.pontual_telemetria.pontual_monitor_api.domain.repository.LocationRepository;
import com.pontual_telemetria.pontual_monitor_api.web.dto.monitoring.instantaneousflowtate.InstantaneousFlowRateRequestDTO;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InstantaneousFlowRateDomainService {

    private final InstantaneousFlowRateRepository instantaneousFlowRateRepository;
    private final ApplicationEventPublisher publisher;
    private final LocationRepository locationRepository;

    @Transactional
    public void create(InstantaneousFlowRateRequestDTO instantaneousFlowRateRequestDTO) {
        Location location = locationRepository.findByExternalId(instantaneousFlowRateRequestDTO.getLocationId());

        if(location == null){
            throw new PontualMonitorException("Localização não informada", "ID_LOCATION_NOT_SPECIFIED", HttpStatus.BAD_REQUEST, "É necessário informar o id da Localização");
        }

        InstantaneousFlowRate entity = InstantaneousFlowRate.builder()
                .location(location)
                .externalId(location.getExternalId())
                .measurement(instantaneousFlowRateRequestDTO.getMeasurement())
                .startDate(instantaneousFlowRateRequestDTO.getStartDate())
                .endDate(instantaneousFlowRateRequestDTO.getEndDate())
                .build();

        instantaneousFlowRateRepository.save(entity);
        publisher.publishEvent(new RefreshNeededEvent(location.getId()));
    }

    @Transactional
    public void delete(Long id) {
        InstantaneousFlowRate instantaneousFlowRate = instantaneousFlowRateRepository.findById(id)
                .orElseThrow(() -> new PontualMonitorException(
                                "Vazão instantânea não encontrada",
                                "INSTANTANEOUS_FLOW_RATE_NOT_FOUND",
                                HttpStatus.BAD_REQUEST,
                                "A vazão instantânea informada não foi encontrada"
                        )
                );

        Long locId = instantaneousFlowRate.getLocation().getId();

        instantaneousFlowRateRepository.delete(instantaneousFlowRate);
        publisher.publishEvent(new RefreshNeededEvent(locId));
    }
}
