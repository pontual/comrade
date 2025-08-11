package com.pontual_telemetria.pontual_monitor_api.application.mapper;

import com.pontual_telemetria.pontual_monitor_api.domain.model.monitoring.ControlReading;
import com.pontual_telemetria.pontual_monitor_api.web.dto.monitoring.control.ControlReadingRequestDTO;
import com.pontual_telemetria.pontual_monitor_api.web.dto.monitoring.control.ControlReadingResponseDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ControlReadingMapper {
    List<ControlReading> toEntityList(List<ControlReadingRequestDTO> controlReadingRequestDTO);
    List<ControlReadingResponseDTO> toDtoList(List<ControlReading> controlReadings);
}
