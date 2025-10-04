package com.pontual_telemetria.pontual_monitor_api.web.controller;

import com.pontual_telemetria.pontual_monitor_api.application.service.DashboardApplicationService;
import com.pontual_telemetria.pontual_monitor_api.web.dto.dashboard.InfoPanelDTO;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardApplicationService dashboardApplicationService;

    @Operation(
            summary = "Recupera listagem de anos com dados presentes",
            description = "Retorna listagem de anos que possuam dados calculos para preenchimento do select"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listagem de anos retornada com sucesso", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = List.class)

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
    @GetMapping("/{externalId}/years")
    public List<Integer> years(@PathVariable Long externalId) {
        return dashboardApplicationService.listAvailableYears(externalId);
    }

    @Operation(
            summary = "Recupera informações para o dashboard por ano",
            description = "Retorna informações compiladas para o Dashboard por ano"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Informações retornadas com sucesso", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = List.class)

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
    @GetMapping("/{externalId}/by-year/{year}")
    public InfoPanelDTO panelByYear(@PathVariable Long externalId, @PathVariable int year) {
        return dashboardApplicationService.dashboardInfoByYear(externalId, year);
    }

    @Operation(
            summary = "Informações gerais do Dashboard",
            description = "Retorna informações compiladas para o Dashboard"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Informações retornadas com sucesso", content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(
                            schema = @Schema(implementation = InfoPanelDTO.class)
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
    @GetMapping("/info/{locationId}")
    ResponseEntity<InfoPanelDTO> dashboardInfo(@PathVariable Long locationId) {
        InfoPanelDTO response = dashboardApplicationService.dashboardInfo(locationId);
        return ResponseEntity.ok(response);
    }
}
