package com.pontual_telemetria.pontual_monitor_api.application.mapper;

import com.pontual_telemetria.pontual_monitor_api.web.dto.sismetro.ControlReadingImportDTO;
import com.pontual_telemetria.pontual_monitor_api.web.dto.telemetry.TelemetryResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TelemetryMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "readingValue", expression = "java(dto.getAdjustedDuration().doubleValue())")
    @Mapping(target = "dtReading", source = "schedule")
    @Mapping(target = "createdAt", source = "schedule")
    @Mapping(target = "createdBy", expression = "java(\"API Telemetria ANA\")")
    @Mapping(target = "observation", expression = "java(String.valueOf(dto.getTransmissionCode()))")
    @Mapping(target = "tag", source = "intervention")
    @Mapping(target = "status", expression = "java(String.valueOf(dto.getTransmissionCode()))")
    @Mapping(target = "average", expression = "java(dto.getAdjustedFlowRate().toPlainString())")
    @Mapping(target = "calculatedVolume", source = "adjustedVolume")
    @Mapping(target = "rawVolume", source = "volume")
    ControlReadingImportDTO toImportDTO(TelemetryResponseDTO dto);

    List<ControlReadingImportDTO> toImportDTOList(List<TelemetryResponseDTO> dtos);
}
