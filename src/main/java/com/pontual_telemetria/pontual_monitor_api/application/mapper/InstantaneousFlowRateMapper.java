package com.pontual_telemetria.pontual_monitor_api.application.mapper;

import com.pontual_telemetria.pontual_monitor_api.domain.model.monitoring.InstantaneousFlowRate;
import com.pontual_telemetria.pontual_monitor_api.web.dto.monitoring.instantaneousflowtate.InstantaneousFlowRateResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InstantaneousFlowRateMapper {
    @Mapping(source = "location.id", target = "locationId")
    InstantaneousFlowRateResponseDTO toDTO(InstantaneousFlowRate entity);

    List<InstantaneousFlowRateResponseDTO> toListDTO(List<InstantaneousFlowRate> entities);
}
