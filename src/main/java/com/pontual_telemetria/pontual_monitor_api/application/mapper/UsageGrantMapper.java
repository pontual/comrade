package com.pontual_telemetria.pontual_monitor_api.application.mapper;

import com.pontual_telemetria.pontual_monitor_api.domain.model.regulatory.UsageGrant;
import com.pontual_telemetria.pontual_monitor_api.web.dto.regulatory.UsageGrantDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UsageGrantMapper {
    @Mapping(source = "location.id", target = "locationId")
    UsageGrantDTO toDto(UsageGrant usageGrant);

    List<UsageGrantDTO> toListDto(List<UsageGrant> usageGrants);
    UsageGrant toEntity(UsageGrantDTO usageGrantDTO);
}
