package com.pontual_telemetria.pontual_monitor_api.infrastructure.client.feign.sgman.dto.patrimonio;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrevisaoHorasDTO {
    private Integer previsao;
    private String statusCalculo;
    private String vigencia;
}
