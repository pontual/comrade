package com.pontual_telemetria.pontual_monitor_api.web.controller;

import com.pontual_telemetria.pontual_monitor_api.application.service.SgmanIntegrationService;
import com.pontual_telemetria.pontual_monitor_api.web.dto.sgman.asset.SgmanAssetDTO;
import com.pontual_telemetria.pontual_monitor_api.web.dto.sgman.requester.SgmanRequesterDTO;
import com.pontual_telemetria.pontual_monitor_api.web.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sgman")
public class SgmanController {

    private final SgmanIntegrationService sgmanIntegrationService;

    @Operation(
            summary = "Lista todos os solicitantes cadastrados na SGMAN",
            description = "Retorna lista de solicitantes"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dados de solicitantes consultados com sucesso", content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(
                            schema = @Schema(implementation = SgmanRequesterDTO.class)
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
    @GetMapping("/requesters")
    ResponseEntity<List<SgmanRequesterDTO>> requestersListAll(){
        List<SgmanRequesterDTO> response = sgmanIntegrationService.requestersListAll();
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Lista todos os patrimônios cadastrados na SGMAN",
            description = "Retorna lista de patrimônios"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dados de patrimônio consultados com sucesso", content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(
                            schema = @Schema(implementation = SgmanAssetDTO.class)
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
    @GetMapping("/assets")
    ResponseEntity<List<SgmanAssetDTO>> assetsListAll(){
        List<SgmanAssetDTO> response = sgmanIntegrationService.assetsListAll();
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Lista os patrimônios cadastrados no SGMAN por id do solicitante",
            description = "Retorna lista de patrimônios por solicitante"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dados de patrimônio por solicitante consultados com sucesso", content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(
                            schema = @Schema(implementation = SgmanAssetDTO.class)
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
    @GetMapping("/asset/findAssetsByRequesterId")
    ResponseEntity<List<SgmanAssetDTO>> findAssetsByRequesterId(@RequestParam("requesterId") Integer requesterId){
        List<SgmanAssetDTO> response = sgmanIntegrationService.findAssetsByRequesterId(requesterId);
        return ResponseEntity.ok(response);
    }
}
