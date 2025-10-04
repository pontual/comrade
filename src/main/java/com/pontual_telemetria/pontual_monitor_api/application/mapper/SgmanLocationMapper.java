package com.pontual_telemetria.pontual_monitor_api.application.mapper;

import com.pontual_telemetria.pontual_monitor_api.infrastructure.client.feign.sgman.dto.patrimonio.PatrimonioDTO;
import com.pontual_telemetria.pontual_monitor_api.web.dto.sgman.location.SgmanLocationDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SgmanLocationMapper {
    List<SgmanLocationDTO> toResponseList(List<PatrimonioDTO> patrimonios);
    SgmanLocationDTO toResponse(PatrimonioDTO patrimonio);
}
