package com.pontual_telemetria.pontual_monitor_api.application.mapper;

import com.pontual_telemetria.pontual_monitor_api.infrastructure.client.feign.sgman.dto.patrimonio.PatrimonioDTO;
import com.pontual_telemetria.pontual_monitor_api.web.dto.sgman.asset.SgmanAssetDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AssetMapper {
    List<SgmanAssetDTO> toResponseList(List<PatrimonioDTO> patrimonios);
    SgmanAssetDTO toResponse(PatrimonioDTO patrimonio);
}
