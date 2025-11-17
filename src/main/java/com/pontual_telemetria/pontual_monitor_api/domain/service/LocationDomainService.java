package com.pontual_telemetria.pontual_monitor_api.domain.service;

import com.pontual_telemetria.pontual_monitor_api.application.mapper.SgmanToLocationMapper;
import com.pontual_telemetria.pontual_monitor_api.domain.model.customer.Location;
import com.pontual_telemetria.pontual_monitor_api.domain.repository.LocationRepository;
import com.pontual_telemetria.pontual_monitor_api.web.dto.sgman.location.SgmanLocationDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationDomainService {

    private final LocationRepository locationRepository;
    private final SgmanToLocationMapper sgmanToLocationMapper;

    @Transactional
    public void updateLocationBySgman(List<SgmanLocationDTO> sgmanLocationDTOs) {
        for (SgmanLocationDTO dto : sgmanLocationDTOs) {

            Location existing = locationRepository.findByExternalId(dto.getId());

            if (existing != null) {
                existing.setDescription(dto.getDescricao());
                existing.setRequesterId(dto.getIdSolicitante());
                existing.setLocationId(dto.getIdLocalizacao());
                existing.setLocationName(dto.getLocalizacao());
                existing.setCategoryId(dto.getIdCategoria());
                existing.setCategory(dto.getCategoria());
                existing.setTypeTechId(dto.getIdTipoTec());
                existing.setTypeTech(dto.getTipoTec());
                existing.setObservation(dto.getObservacao());
                existing.setBrandId(dto.getIdMarca());
                existing.setBrand(dto.getMarca());
                existing.setModelId(dto.getIdModelo());
                existing.setModel(dto.getModelo());
                existing.setSerial(dto.getSerial());
                existing.setPatrimony(dto.getPatrimonio());
                existing.setTag(dto.getTag());
                existing.setDataMatrix(dto.getDataMatrix());
                existing.setDetails(dto.getDetalhes());
                existing.setStatus(dto.getStatus());
                existing.setSituation(dto.getSituacao());
                existing.setSituationId(dto.getIdSituacao());
                existing.setUpdatedAt(LocalDateTime.now());

                locationRepository.save(existing);

            } else {
                Location location = sgmanToLocationMapper.toEntity(dto);
                location.setExternalId(dto.getId());
                location.setIncludedAt(LocalDateTime.now());
                location.setUpdatedAt(LocalDateTime.now());

                locationRepository.save(location);
            }
        }
    }
}
