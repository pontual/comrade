package com.pontual_telemetria.pontual_monitor_api.web.controller;

import com.pontual_telemetria.pontual_monitor_api.application.service.OperationSummaryApplicationService;
import com.pontual_telemetria.pontual_monitor_api.web.dto.dashboard.OperationSummaryDTO;
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
@RequestMapping("/operation-summary")
public class OperationSummaryController {

    private final OperationSummaryApplicationService operationSummaryApplicationService;

    @Operation(
            summary = "Informações resumidas do Dashboard",
            description = "Retorna informações resumidas para os cards no Dashboard"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Informações retornadas com sucesso", content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(
                            schema = @Schema(implementation = OperationSummaryDTO.class)
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
    @GetMapping("/year/by-location")
    public ResponseEntity<List<OperationSummaryDTO>> getOperationSummaryByLocationId(@RequestParam Long locationId, @RequestParam(required = false) Integer year, @RequestParam(name = "awaitFresh", defaultValue = "false") boolean awaitFresh) {
        List<OperationSummaryDTO> response = operationSummaryApplicationService.getOperationSummaryByLocationId(locationId, year, awaitFresh);
        return ResponseEntity.ok(response);
    }
}
