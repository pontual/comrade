package com.pontual_telemetria.pontual_monitor_api.application.mapper;

import com.pontual_telemetria.pontual_monitor_api.domain.model.customer.Location;
import com.pontual_telemetria.pontual_monitor_api.web.dto.location.LocationDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LocationMapper {
    List<LocationDTO> toListDto(List<Location> locations);
}
