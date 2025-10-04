package com.pontual_telemetria.pontual_monitor_api.web.controller;

import com.pontual_telemetria.pontual_monitor_api.application.service.InstantaneousFlowRateApplicationService;
import com.pontual_telemetria.pontual_monitor_api.web.dto.monitoring.instantaneousflowtate.InstantaneousFlowRateRequestDTO;
import com.pontual_telemetria.pontual_monitor_api.web.dto.monitoring.instantaneousflowtate.InstantaneousFlowRateResponseDTO;
import com.pontual_telemetria.pontual_monitor_api.web.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/instantaneous-flow-rate")
public class InstantaneousFlowRateController {

    private final InstantaneousFlowRateApplicationService instantaneousFlowRateApplicationService;

    @Operation(
            summary = "Lista vazões instantâneas cadastradas",
            description = "Retorna lista com vazões instantâneas cadastradas"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vazões instantâneas retornadas com sucesso", content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(
                            schema = @Schema(implementation = InstantaneousFlowRateResponseDTO.class)
                    )
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
    @GetMapping("/{locationId}")
    public ResponseEntity<List<InstantaneousFlowRateResponseDTO>> getAllByLocationId(@PathVariable Long locationId){
        List<InstantaneousFlowRateResponseDTO> response = instantaneousFlowRateApplicationService.getAllByLocationId(locationId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Cadastrar vazão instantânea",
            description = "Realiza cadastro de vazão instantânea"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Vazão instantânea cadastrada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
            )),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
            ))
    })
    @PostMapping()
    public ResponseEntity<Void> create(@RequestBody @Valid InstantaneousFlowRateRequestDTO instantaneousFlowRateRequestDTO){
        instantaneousFlowRateApplicationService.create(instantaneousFlowRateRequestDTO);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Deletar vazão instantânea",
            description = "Realiza exclusão de vazão instantânea"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Vazão instantânea excluída com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
            )),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
            ))
    })
    @DeleteMapping("/{externalId}")
    public ResponseEntity<Void> delete(@PathVariable Long externalId) {
        instantaneousFlowRateApplicationService.delete(externalId);
        return ResponseEntity.noContent().build();
    }
}
