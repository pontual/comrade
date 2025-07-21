package com.pontual_telemetria.pontual_monitor_api.application.mapper;

import com.pontual_telemetria.pontual_monitor_api.domain.model.monitoring.Device;
import com.pontual_telemetria.pontual_monitor_api.web.dto.monitoring.DeviceDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DeviceMapper {
    List<DeviceDTO> toListDto(List<Device> devices);
    Device toEntity(DeviceDTO deviceDTO);
}
