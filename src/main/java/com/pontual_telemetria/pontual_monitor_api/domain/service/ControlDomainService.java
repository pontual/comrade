package com.pontual_telemetria.pontual_monitor_api.domain.service;

import com.pontual_telemetria.pontual_monitor_api.application.mapper.ControlReadingMapper;
import com.pontual_telemetria.pontual_monitor_api.domain.exception.PontualMonitorException;
import com.pontual_telemetria.pontual_monitor_api.domain.model.customer.Location;
import com.pontual_telemetria.pontual_monitor_api.domain.model.monitoring.Control;
import com.pontual_telemetria.pontual_monitor_api.domain.model.monitoring.ControlReading;
import com.pontual_telemetria.pontual_monitor_api.domain.model.monitoring.Device;
import com.pontual_telemetria.pontual_monitor_api.domain.repository.ControlReadingDataRepository;
import com.pontual_telemetria.pontual_monitor_api.domain.repository.ControlRepository;
import com.pontual_telemetria.pontual_monitor_api.domain.repository.DeviceRepository;
import com.pontual_telemetria.pontual_monitor_api.domain.repository.LocationRepository;
import com.pontual_telemetria.pontual_monitor_api.web.dto.control.ControlDTO;
import com.pontual_telemetria.pontual_monitor_api.web.dto.control.ControlReadingRequestDTO;
import com.pontual_telemetria.pontual_monitor_api.web.dto.control.ControlReadingResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ControlDomainService {
    private final LocationRepository locationRepository;
    private final ControlRepository controlRepository;
    private final DeviceRepository deviceRepository;
    private final ControlReadingDataRepository controlReadingDataRepository;
    private final ControlReadingMapper controlReadingMapper;


    public void create(ControlDTO controlDTO){
        Location location = locationRepository.findByExternalId(controlDTO.getLocationId());

        if(location == null){
            throw new PontualMonitorException("Localização não informada", "ID_LOCATION_NOT_SPECIFIED", HttpStatus.BAD_REQUEST, "É necessário informar o id da Localização");
        }

        Control control = Control.builder()
                .location(location)
                .deviceId(controlDTO.getDeviceId())
                .dtDeviceActivate(controlDTO.getDtDeviceActivate())
                .active(controlDTO.getActive())
                .build();

        controlRepository.save(control);
        updateLinkDevice(controlDTO.getDeviceId(), location.getLocationName());
    }

    public void disable(Long id){
        Control control = controlRepository.findById(id).orElse(null);
        LocalDateTime now = LocalDateTime.now();

        if(control != null){
            control.setActive(false);
            control.setDtDeviceDeactivation(now);
            controlRepository.save(control);
            updateLinkDevice(control.getDeviceId());
        } else {
            throw new PontualMonitorException(
                    "Controle não encontrado",
                    "CONTROL_NOT_FOUND",
                    HttpStatus.BAD_REQUEST,
                    "Não foi possível encontrar o controle informado"
            );
        }
    }

    public void updateLinkDevice(String deviceId){
        Device device = deviceRepository.getDeviceByIdentifier(deviceId);

        if(device != null){
            device.setLinkedPatrimony(null);
            deviceRepository.save(device);
        }
    }

    public List<ControlReadingResponseDTO> getReadingDataByLocationId(Long locationId) {
        List<ControlReading> controlReadings = controlReadingDataRepository.findAllByLocationId(locationId);

        if(controlReadings.isEmpty()) {
            return new ArrayList<>();
        }

        return controlReadingMapper.toDtoList(controlReadings);
    }

    public void updateLinkDevice(String identifier, String locationName) {
        Device device = deviceRepository.getDeviceByIdentifier(identifier);

        if(device != null) {
            device.setLinkedPatrimony(locationName);
            deviceRepository.save(device);
        }
    }

    public void createReadingData(List<ControlReadingRequestDTO> controlReadingRequestDTO){
        if(controlReadingRequestDTO == null || controlReadingRequestDTO.isEmpty()){
            throw new PontualMonitorException(
                    "A lista de dados está vazia",
                    "EMPTY_DATA_LIST",
                    HttpStatus.BAD_REQUEST,
                    "A listagem de dados está vazia"
            );
        }
        Long controlId = controlReadingRequestDTO.getFirst().getControlId();
        Integer locationId = controlReadingRequestDTO.getFirst().getLocationId();

        Control control = controlRepository.findById(controlId)
                .orElseThrow(() -> new IllegalArgumentException("Controle não encontrado para o ID: " + controlId));

        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new IllegalArgumentException("Localização não encontrado para o ID: " + locationId));

        List<ControlReading> list = controlReadingMapper.toEntityList(controlReadingRequestDTO);
        for(ControlReading reading : list) {
            reading.setControl(control);
            reading.setLocation(location);
        }

        controlReadingDataRepository.saveAll(list);
    }
}
