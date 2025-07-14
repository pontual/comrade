package com.pontual_telemetria.pontual_monitor_api.application.mapper;

import com.pontual_telemetria.pontual_monitor_api.domain.model.customer.Requester;
import com.pontual_telemetria.pontual_monitor_api.infrastructure.client.feign.sgman.dto.solicitante.SolicitanteDTO;
import com.pontual_telemetria.pontual_monitor_api.web.dto.requester.RequesterDTO;
import com.pontual_telemetria.pontual_monitor_api.web.dto.sgman.requester.SgmanRequesterDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RequesterMapper {
    List<SgmanRequesterDTO> toResponseList(List<SolicitanteDTO> solicitantes);
    List<RequesterDTO> toDtoList(List<Requester> requesters);
}
