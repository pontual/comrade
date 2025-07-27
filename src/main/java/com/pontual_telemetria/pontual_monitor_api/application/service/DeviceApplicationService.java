package com.pontual_telemetria.pontual_monitor_api.application.service;

import com.pontual_telemetria.pontual_monitor_api.application.mapper.DeviceMapper;
import com.pontual_telemetria.pontual_monitor_api.domain.repository.DeviceRepository;
import com.pontual_telemetria.pontual_monitor_api.domain.service.DeviceDomainService;
import com.pontual_telemetria.pontual_monitor_api.web.dto.monitoring.DeviceDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceApplicationService {

    private final DeviceRepository deviceRepository;
    private final DeviceDomainService deviceDomainService;
    private final DeviceMapper deviceMapper;

    public List<DeviceDTO> getDevices() {
        log.info("[GET-DEVICES] Recuperando lista de dispositivos");
        List<DeviceDTO> devices = deviceMapper.toListDto(deviceRepository.findAll());
        log.info("[GET-DEVICES] Lista de dispositivos recuperados com sucesso");
        return devices;
    }

    public void create(DeviceDTO deviceDTO){
        log.info("[CREATE-DEVICE] Cadastrado novo dispositivo");
        deviceDomainService.create(deviceDTO);
        log.info("[CREATE-DEVICE] Cadastro de novo dispositivo realizado com sucesso");
    }

    public void update(Long id, boolean status){
        log.info("[UPDATE-DEVICE] Atualizando situacao dispositivo");
        deviceDomainService.update(id, status);
        log.info("[UPDATE-DEVICE] Situação dispositivo do atualizada com sucesso");
    }

    public void delete(Long id){
        log.info("[DELETE-DEVICE] Inciada exclusão de dispositivo id={}", id);
        deviceDomainService.delete(id);
        log.info("[DELETE-DEVICE] Dispositivo excluído com sucesso id={}", id);
    }
}
