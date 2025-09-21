package com.pontual_telemetria.pontual_monitor_api.web.controller;

import com.pontual_telemetria.pontual_monitor_api.application.service.DailyCalculatedApplicationService;
import com.pontual_telemetria.pontual_monitor_api.application.service.DailyHoursOverrideApplicationService;
import com.pontual_telemetria.pontual_monitor_api.web.dto.monitoring.dailyoperation.DailyCalculatedItemDTO;
import com.pontual_telemetria.pontual_monitor_api.web.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/monitoring/{externalId}")
public class DailyHoursOverrideController {

    private final DailyHoursOverrideApplicationService dailyHoursOverrideApplicationService;
    private final DailyCalculatedApplicationService dailyCalculatedApplicationService;


    @Operation(
            summary = "Recupera dados diários de operação ",
            description = "Retorna listagem de dados diários de operação passíveis de edição"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso", content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(
                            schema = @Schema(implementation = DailyCalculatedItemDTO.class)
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
    @GetMapping(value = "/daily-hours", produces = "application/json")
    public ResponseEntity<List<DailyCalculatedItemDTO>> getCalculated(
            @PathVariable Long externalId
    ) {
        return ResponseEntity.ok(dailyCalculatedApplicationService.getCalculated(externalId));
    }


    @Operation(
            summary = "Realiza sobrescrita de dados diários",
            description = "Realiza sobrescrita de dados de operação diária"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Dados atualizados com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
            )),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
            ))
    })
    @PutMapping("/{day}")
    public ResponseEntity<Void> upsert(
            @PathVariable Long externalId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate day,
            @RequestParam("hours") BigDecimal hours,
            @RequestParam("updatedBy") String updatedBy
    ) {
        dailyHoursOverrideApplicationService.upsert(externalId, day, hours, updatedBy);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Realiza a exclusão de dados diários",
            description = "Realiza exclusão de dados de operação diária"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Dados excluídos com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
            )),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
            ))
    })
    @DeleteMapping("/{day}")
    public ResponseEntity<Void> delete(
            @PathVariable Long externalId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate day
    ) {
        dailyHoursOverrideApplicationService.delete(externalId, day);
        return ResponseEntity.noContent().build();
    }
}
