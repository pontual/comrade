package com.pontual_telemetria.pontual_monitor_api.domain.service;

import com.pontual_telemetria.pontual_monitor_api.application.mapper.ControlReadingMapper;
import com.pontual_telemetria.pontual_monitor_api.domain.exception.PontualMonitorException;
import com.pontual_telemetria.pontual_monitor_api.domain.model.customer.Location;
import com.pontual_telemetria.pontual_monitor_api.domain.model.monitoring.Control;
import com.pontual_telemetria.pontual_monitor_api.domain.model.monitoring.ControlReading;
import com.pontual_telemetria.pontual_monitor_api.domain.repository.ControlReadingDataRepository;
import com.pontual_telemetria.pontual_monitor_api.domain.repository.ControlRepository;
import com.pontual_telemetria.pontual_monitor_api.domain.repository.LocationRepository;
import com.pontual_telemetria.pontual_monitor_api.web.dto.control.ControlDTO;
import com.pontual_telemetria.pontual_monitor_api.web.dto.control.ControlReadingRequesterDTO;
import com.pontual_telemetria.pontual_monitor_api.web.dto.control.ControlReadingResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ControlDomainService {
    private final LocationRepository locationRepository;
    private final ControlRepository controlRepository;
    private final ControlReadingDataRepository controlReadingDataRepository;
    private final ControlReadingMapper controlReadingMapper;


    public void create(ControlDTO controlDTO){
        Location location = locationRepository.findByExternalId(controlDTO.getLocationId());

        Control control = Control.builder()
                .location(location)
                .deviceId(controlDTO.getDeviceId())
                .deviceStatus(controlDTO.getDeviceStatus())
                .dtDeviceActivate(controlDTO.getDtDeviceActivate())
                .active(controlDTO.getActive())
                .build();

        controlRepository.save(control);
    }

    public List<ControlReadingResponseDTO> getReadingDataByLocationId(Long locationId) {
        List<ControlReading> controlReadings = controlReadingDataRepository.findAllByLocationId(locationId);

        if(controlReadings.isEmpty()) {
            return new ArrayList<>();
        }

        return controlReadingMapper.toDtoList(controlReadings);
    }

    public void createReadingData(List<ControlReadingRequesterDTO> controlReadingRequesterDTO){
        if(controlReadingRequesterDTO == null || controlReadingRequesterDTO.isEmpty()){
            throw new PontualMonitorException(
                    "A lista de dados está vazia",
                    "EMPTY_DATA_LIST",
                    HttpStatus.BAD_REQUEST,
                    "A listagem de dados está vazia"
            );
        }
        Long controlId = controlReadingRequesterDTO.getFirst().getControlId();
        Integer locationId = controlReadingRequesterDTO.getFirst().getLocationId();

        Control control = controlRepository.findById(controlId)
                .orElseThrow(() -> new IllegalArgumentException("Controle não encontrado para o ID: " + controlId));

        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new IllegalArgumentException("Localização não encontrado para o ID: " + locationId));

        List<ControlReading> list = controlReadingMapper.toEntityList(controlReadingRequesterDTO);
        for(ControlReading reading : list) {
            reading.setControl(control);
            reading.setLocation(location);
        }

        controlReadingDataRepository.saveAll(list);
    }
}
