package com.pontual_telemetria.pontual_monitor_api.web.controller;

import com.pontual_telemetria.pontual_monitor_api.application.service.TelemetryApplicationService;
import com.pontual_telemetria.pontual_monitor_api.web.dto.sismetro.ControlReadingImportDTO;
import com.pontual_telemetria.pontual_monitor_api.web.dto.telemetry.TelemetryRequestDTO;
import com.pontual_telemetria.pontual_monitor_api.web.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/telemetry/external")
public class TelemetryController {

    private final TelemetryApplicationService service;

    @Operation(
            summary = "Listar leituras de telemetria",
            description = "Retorna todas as leituras de telemetria de uma intervenção específica"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Leituras retornadas com sucesso", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ControlReadingImportDTO.class)
            )),
            @ApiResponse(responseCode = "400", description = "Intervenção inválida ou não informada", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
            )),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
            ))
    })
    @PostMapping("/readings")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<List<ControlReadingImportDTO>> getReadings(@RequestBody TelemetryRequestDTO request) {
        List<ControlReadingImportDTO> response = service.getReadings(request);
        return ResponseEntity.ok(response);
    }
}
