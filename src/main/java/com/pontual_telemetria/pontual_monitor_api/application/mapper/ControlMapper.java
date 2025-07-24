package com.pontual_telemetria.pontual_monitor_api.application.mapper;

import com.pontual_telemetria.pontual_monitor_api.domain.model.monitoring.Control;
import com.pontual_telemetria.pontual_monitor_api.web.dto.control.ControlDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ControlMapper {
    List<ControlDTO> toListDto(List<Control> controls);
    Control toEntity(ControlDTO controlDTO);
}
