package com.pontual_telemetria.pontual_monitor_api.web.controller;

import com.pontual_telemetria.pontual_monitor_api.web.dto.regulatory.UsageGrantDTO;
import com.pontual_telemetria.pontual_monitor_api.web.dto.regulatory.UsageGrantRequestDTO;
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
@RequestMapping("/usage-grant")
public class UsageGrantController {

    private final UsageGrantApplicationService usageGrantApplicationService;

    @Operation(
            summary = "Lista outorgas anuais cadastrados",
            description = "Retorna lista com outorgas anuais cadastradas"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Outorgas retornados com sucesso", content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(
                            schema = @Schema(implementation = UsageGrantDTO.class)
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
    public ResponseEntity<List<UsageGrantDTO>> getAllByLocationId(@PathVariable Long locationId) {
        List<UsageGrantDTO> response = usageGrantApplicationService.getAllByLocationId(locationId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Cadastra outorga anual",
            description = "Realiza o cadastramento de outorga anual"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Outorga cadastrada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
            )),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
            ))
    })
    @PostMapping("/create")
    public ResponseEntity<Void> create(@RequestBody @Valid UsageGrantRequestDTO usageGrantRequestDTO) {
        usageGrantApplicationService.create(usageGrantRequestDTO);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Deleta outorga anual",
            description = "Apaga outorga anual por id"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Outorga apagada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
            )),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
            ))
    })
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        usageGrantApplicationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
