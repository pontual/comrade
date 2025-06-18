package com.pontual_telemetria.pontual_monitor_api.infrastructure.client.feign.sgman.dto.solicitante;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitanteResponseDTO {
    private boolean valid;
    private String message;
    private int page;
    private int pageSize;
    private int totalPages;
    private int resultSize;
    private List<SolicitanteDTO> resultList;
}
