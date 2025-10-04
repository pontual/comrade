package com.pontual_telemetria.pontual_monitor_api.infrastructure.client.feign.sgman;

import com.pontual_telemetria.pontual_monitor_api.infrastructure.client.feign.sgman.dto.patrimonio.PatrimonioResponseDTO;
import com.pontual_telemetria.pontual_monitor_api.infrastructure.client.feign.sgman.dto.solicitante.SolicitanteResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "sgmanClient", url = "${sgman.client.url}")
public interface SgmanClient {

    @GetMapping("/solicitantes/listAll")
    SolicitanteResponseDTO listAllSolicitantes(
            @RequestHeader("unit") String unit,
            @RequestHeader("key") String key
    );

    @GetMapping("/patrimonios/listAll")
    PatrimonioResponseDTO listAllPatrimonios(
            @RequestHeader("unit") String unit,
            @RequestHeader("key") String key
    );

    @GetMapping("/patrimonios/findBySolicitante")
    PatrimonioResponseDTO listPatrimonioBySolicitante(
            @RequestParam("solicitante") Integer idSolicitante,
            @RequestHeader("unit") String unit,
            @RequestHeader("key") String key
    );
}
