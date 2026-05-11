package com.pontual_telemetria.pontual_monitor_api.infrastructure.client.feign.telemetria;

import com.pontual_telemetria.pontual_monitor_api.infrastructure.client.feign.telemetria.dto.TelemetryAuthRequestDTO;
import com.pontual_telemetria.pontual_monitor_api.infrastructure.client.feign.telemetria.dto.TelemetryAuthResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "telemetriaClient", url = "${telemetria.client.url}")
public interface TelemetriaClient {

    @PostMapping("/auth")
    TelemetryAuthResponseDTO getAuthToken(@RequestBody TelemetryAuthRequestDTO telemetryAuthRequestDTO);

    @GetMapping("/telemetria/externa/leituras")
    String getLeiturasPorIntervencao(
            @RequestHeader("accept") String acceptType,
            @RequestHeader("Authorization") String authorization,
            @RequestParam("intervencoes") String intervencoes,
            @RequestParam("dataInicio") String dataInicio,
            @RequestParam("dataFim") String dataFim
    );
}
