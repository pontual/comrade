package com.pontual_telemetria.pontual_monitor_api.infrastructure.client.feign.sgman.dto.patrimonio;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatrimonioResponseDTO {
    private Integer page;
    private Integer pageSize;
    private Integer totalPages;
    private Integer resultSize;
    private List<PatrimonioDTO> resultList;
}
