package com.pontual_telemetria.pontual_monitor_api.domain.service;

import com.pontual_telemetria.pontual_monitor_api.application.mapper.DeviceMapper;
import com.pontual_telemetria.pontual_monitor_api.domain.exception.PontualMonitorException;
import com.pontual_telemetria.pontual_monitor_api.domain.model.monitoring.Device;
import com.pontual_telemetria.pontual_monitor_api.domain.repository.DeviceRepository;
import com.pontual_telemetria.pontual_monitor_api.web.dto.monitoring.DeviceDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DeviceDomainService {

    private final DeviceRepository deviceRepository;
    private final DeviceMapper deviceMapper;

    public void create(DeviceDTO deviceDTO) {
        LocalDateTime now = LocalDateTime.now();
        boolean isDeviceExists = deviceRepository.existsByIdentifier(deviceDTO.getIdentifier());

        if(!isDeviceExists){
            Device newDevice = deviceMapper.toEntity(deviceDTO);
            newDevice.setCreatedAt(now);
            deviceRepository.save(newDevice);
        } else {
            throw new PontualMonitorException(
                    "Device já existente",
                    "DEVICE_ALREADY_EXISTS",
                    HttpStatus.BAD_REQUEST,
                    "Já existe um dispositivo cadastrado com esse identificador"
            );
        }
    }

    public void delete(Long id){
        Device device = deviceRepository.getDeviceById(id);
        deviceRepository.delete(device);
    }
}
