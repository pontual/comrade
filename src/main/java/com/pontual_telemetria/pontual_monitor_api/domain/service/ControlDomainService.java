package com.pontual_telemetria.pontual_monitor_api.domain.service;

import com.pontual_telemetria.pontual_monitor_api.application.mapper.ControlReadingMapper;
import com.pontual_telemetria.pontual_monitor_api.domain.event.RefreshNeededEvent;
import com.pontual_telemetria.pontual_monitor_api.domain.exception.PontualMonitorException;
import com.pontual_telemetria.pontual_monitor_api.domain.model.customer.Location;
import com.pontual_telemetria.pontual_monitor_api.domain.model.monitoring.Control;
import com.pontual_telemetria.pontual_monitor_api.domain.model.monitoring.ControlReading;
import com.pontual_telemetria.pontual_monitor_api.domain.model.monitoring.Device;
import com.pontual_telemetria.pontual_monitor_api.domain.repository.ControlReadingDataRepository;
import com.pontual_telemetria.pontual_monitor_api.domain.repository.ControlRepository;
import com.pontual_telemetria.pontual_monitor_api.domain.repository.DeviceRepository;
import com.pontual_telemetria.pontual_monitor_api.domain.repository.LocationRepository;
import com.pontual_telemetria.pontual_monitor_api.web.dto.monitoring.control.ControlDTO;
import com.pontual_telemetria.pontual_monitor_api.web.dto.monitoring.control.ControlReadingRequestDTO;
import com.pontual_telemetria.pontual_monitor_api.web.dto.monitoring.control.ControlReadingResponseDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class ControlDomainService {

    private static final String API_ANA_TELEMETRY = "API Telemetria ANA";
    private final LocationRepository locationRepository;
    private final ControlRepository controlRepository;
    private final DeviceRepository deviceRepository;
    private final ControlReadingDataRepository controlReadingDataRepository;
    private final ControlReadingMapper controlReadingMapper;
    private final TelemetryDomainService telemetryDomainService;
    private final ApplicationEventPublisher publisher;

    @Transactional
    public void create(ControlDTO controlDTO){
        boolean isCreateOperation = true;
        Location location = locationRepository.findByExternalId(controlDTO.getLocationId());

        if(location == null){
            throw new PontualMonitorException("Localização não informada", "ID_LOCATION_NOT_SPECIFIED", HttpStatus.BAD_REQUEST, "É necessário informar o id da Localização");
        }

        Control control = Control.builder()
                .location(location)
                .externalId(location.getExternalId())
                .deviceId(controlDTO.getDeviceId())
                .dtDeviceActivate(controlDTO.getDtDeviceActivate())
                .active(controlDTO.getActive())
                .isFonteDadosApiAna(controlDTO.getIsFonteDadosApiAna())
                .build();

        controlRepository.save(control);
        updateLinkDevice(isCreateOperation, controlDTO.getDeviceId(), location.getLocationName());
    }

    @Transactional
    public void disable(Long id){
        boolean isCreateOperation = false;
        Control control = controlRepository.findById(id).orElse(null);
        LocalDateTime now = LocalDateTime.now();

        if(control != null){
            control.setActive(false);
            control.setDtDeviceDeactivation(now);
            controlRepository.save(control);
            updateLinkDevice(isCreateOperation, control.getDeviceId(), null);
        } else {
            throw new PontualMonitorException(
                    "Controle não encontrado",
                    "CONTROL_NOT_FOUND",
                    HttpStatus.BAD_REQUEST,
                    "Não foi possível encontrar o controle informado"
            );
        }
    }

    @Transactional
    public void updateLinkDevice(boolean isCreateOperation, String identifier, String locationName) {
        Device device = deviceRepository.getDeviceByIdentifier(identifier);

        if(device != null) {
            if(isCreateOperation){
                device.setLinkedPatrimony(locationName);
            } else {
                device.setLinkedPatrimony(null);
            }
            deviceRepository.save(device);
        }
    }

    private void recalculateVolumeForTag(String tag) {
        List<ControlReading> all = controlReadingDataRepository
                .findAllByTagAndCreatedByOrderByDtReadingAsc(tag, API_ANA_TELEMETRY);

        BigDecimal prevRawVolume = null;
        List<ControlReading> toUpdate = new ArrayList<>();

        for (ControlReading r : all) {
            if (r.getRawVolume() == null) {
                prevRawVolume = null;
                continue;
            }

            BigDecimal newCalcVolume = prevRawVolume == null
                    ? r.getRawVolume()
                    : r.getRawVolume().subtract(prevRawVolume).max(BigDecimal.ZERO);

            if (r.getCalculatedVolume() == null || newCalcVolume.compareTo(r.getCalculatedVolume()) != 0) {
                r.setCalculatedVolume(newCalcVolume);
                r.setUpdatedAt(LocalDateTime.now());
                toUpdate.add(r);
            }

            prevRawVolume = r.getRawVolume();
        }

        if (!toUpdate.isEmpty()) {
            controlReadingDataRepository.saveAll(toUpdate);
            log.info("[TELEMETRY] recalculo de volume para tag {}: {} registros atualizados", tag, toUpdate.size());
        }
    }

    private void recalculateAccumulatedHoursForTag(String tag) {
        List<ControlReading> all = controlReadingDataRepository
                .findAllByTagAndCreatedByOrderByDtReadingAsc(tag, API_ANA_TELEMETRY);

        List<ControlReading> toUpdate = telemetryDomainService.recalculateAccumulatedHours(all);

        if (!toUpdate.isEmpty()) {
            controlReadingDataRepository.saveAll(toUpdate);
            log.info("[TELEMETRY] recalculo de horas acumuladas para tag {}: {} registros atualizados", tag, toUpdate.size());
        }
    }

    public List<ControlReadingResponseDTO> getReadingDataByLocationId(Long locationId) {
        List<ControlReading> controlReadings = controlReadingDataRepository.findAllByLocationId(locationId);

        if(controlReadings.isEmpty()) {
            return new ArrayList<>();
        }

        return controlReadingMapper.toDtoList(controlReadings);
    }

    @Transactional
    public void createReadingData(List<ControlReadingRequestDTO> controlReadingRequestDTO){
        if(controlReadingRequestDTO == null || controlReadingRequestDTO.isEmpty()){
            throw new PontualMonitorException(
                    "A lista de dados está vazia",
                    "EMPTY_DATA_LIST",
                    HttpStatus.BAD_REQUEST,
                    "A listagem de dados está vazia"
            );
        }

        Long controlId  = controlReadingRequestDTO.getFirst().getControlId();
        Integer locationId = controlReadingRequestDTO.getFirst().getLocationId();

        Control control = controlRepository.findById(controlId)
                .orElseThrow(() -> new IllegalArgumentException("Controle não encontrado para o ID: " + controlId));

        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new IllegalArgumentException("Localização não encontrado para o ID: " + locationId));

        List<ControlReading> list = controlReadingMapper.toEntityList(controlReadingRequestDTO);

        LocalDateTime minTs = list.stream().map(ControlReading::getDtReading)
                .min(LocalDateTime::compareTo).orElseThrow();
        LocalDateTime maxTs = list.stream().map(ControlReading::getDtReading)
                .max(LocalDateTime::compareTo).orElseThrow();

        var existing = new HashSet<>(
                controlReadingDataRepository.findExistingInstantsInRangeByLocation(Math.toIntExact(location.getId()), minTs, maxTs)
        );

        var seenInBatch = new HashSet<LocalDateTime>();

        List<ControlReading> toInsert = list.stream()
                .filter(r -> seenInBatch.add(r.getDtReading()))
                .filter(r -> !existing.contains(r.getDtReading()))
                .peek(r -> {
                    r.setControl(control);
                    r.setLocation(location);
                    r.setExternalId(location.getExternalId());
                    r.setCreatedAt(LocalDateTime.now());
                })
                .toList();

        if (!toInsert.isEmpty()) {
            controlReadingDataRepository.saveAllAndFlush(toInsert);

            Set<String> anaTags = new HashSet<>();
            for (ControlReading r : toInsert) {
                if (API_ANA_TELEMETRY.equals(r.getCreatedBy()) && r.getTag() != null) {
                    anaTags.add(r.getTag());
                }
            }
            for (String tag : anaTags) {
                recalculateVolumeForTag(tag);
                recalculateAccumulatedHoursForTag(tag);
            }
        }

        publisher.publishEvent(new RefreshNeededEvent(location.getId()));
    }
}
