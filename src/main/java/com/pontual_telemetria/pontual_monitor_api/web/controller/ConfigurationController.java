package com.pontual_telemetria.pontual_monitor_api.web.controller;

import com.pontual_telemetria.pontual_monitor_api.application.service.ConfigurationApplicationService;
import com.pontual_telemetria.pontual_monitor_api.web.dto.configuration.FunctionDTO;
import com.pontual_telemetria.pontual_monitor_api.web.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/configuration")
public class ConfigurationController {

    private final ConfigurationApplicationService service;

    @Operation(
            summary = "Listar funcionalidades de sistema",
            description = "Retorna lista de funcionalidades de sistema cadastradas"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de funcionalidades retornada com sucesso", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = FunctionDTO.class)
            )),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
            )),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
            ))
    })
    @GetMapping()
    public ResponseEntity<List<FunctionDTO>> functions() {
        return ResponseEntity.ok(service.functions());
    }

    @Operation(
            summary = "Atualização de funcionalidade",
            description = "Atualiza status de funcionalidade por Id"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Funcionalidade atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
            )),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
            ))
    })
    @PutMapping("/{id}/{enabled}")
    public ResponseEntity<Void> update(@PathVariable Long id, @PathVariable Boolean enabled) {
        service.update(id, enabled);
        return ResponseEntity.noContent().build();
    }
}
