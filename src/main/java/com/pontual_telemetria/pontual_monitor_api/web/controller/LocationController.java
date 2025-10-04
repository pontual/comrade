package com.pontual_telemetria.pontual_monitor_api.web.controller;

import com.pontual_telemetria.pontual_monitor_api.application.service.LocationApplicationService;
import com.pontual_telemetria.pontual_monitor_api.web.dto.location.LocationDTO;
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
@RequestMapping("/locations")
public class LocationController {

    private final LocationApplicationService locationApplicationService;

    @Operation(
            summary = "Listar localizações por id do solicitante ",
            description = "Retorna as localizações referencidas por id do solicitante"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Localizaçoes retornadas com sucesso", content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(
                            schema = @Schema(implementation = LocationDTO.class)
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
    @GetMapping("/{requesterId}")
    public ResponseEntity<List<LocationDTO>> getByRequesterId(@PathVariable Integer requesterId) {
        List<LocationDTO> response = locationApplicationService.getByRequesterId(requesterId);
        return ResponseEntity.ok(response);
    }
}
