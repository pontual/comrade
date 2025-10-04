package com.pontual_telemetria.pontual_monitor_api.application.mapper;

import com.pontual_telemetria.pontual_monitor_api.domain.model.regulatory.UsageGrantMonthly;
import com.pontual_telemetria.pontual_monitor_api.web.dto.regulatory.UsageGrantMonthlyDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UsageGrantMonthlyMapper {
    List<UsageGrantMonthly> toEntity(List<UsageGrantMonthlyDTO> usageGrantMonthlyDTO);
}
