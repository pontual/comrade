package com.pontual_telemetria.pontual_monitor_api.application.mapper;

import com.pontual_telemetria.pontual_monitor_api.domain.model.configuration.Function;
import com.pontual_telemetria.pontual_monitor_api.web.dto.configuration.FunctionDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ConfigurationMapper {
    FunctionDTO toDTO(Function model);
    List<FunctionDTO> toDTO(List<Function> model);
}
