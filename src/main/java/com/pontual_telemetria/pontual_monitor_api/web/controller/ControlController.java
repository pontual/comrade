package com.pontual_telemetria.pontual_monitor_api.web.controller;

import com.pontual_telemetria.pontual_monitor_api.application.service.ControlApplicationService;
import com.pontual_telemetria.pontual_monitor_api.web.dto.control.ControlDTO;
import com.pontual_telemetria.pontual_monitor_api.web.dto.control.ControlReadingRequestDTO;
import com.pontual_telemetria.pontual_monitor_api.web.dto.control.ControlReadingResponseDTO;
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
@RequestMapping("/control")
public class ControlController {

    private final ControlApplicationService controlApplicationService;

    @Operation(
            summary = "Lista controles cadastrados",
            description = "Retorna lista com controles cadastrados"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Controles retornados com sucesso", content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(
                            schema = @Schema(implementation = ControlDTO.class)
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
    @GetMapping("/{externalId}")
    public ResponseEntity<List<ControlDTO>> getControlsByExternalId(@PathVariable Long externalId) {
        List<ControlDTO> response = controlApplicationService.getControlsByExternalId(externalId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Cadastrar controle",
            description = "Realiza cadastro de novo controle"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Controle cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
            )),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
            ))
    })
    @PostMapping("/create")
    public ResponseEntity<Void> create(@RequestBody @Valid ControlDTO controlDTO){
        controlApplicationService.create(controlDTO);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Desativa controle",
            description = "Desativa controle por id"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Controle desativado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
            )),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
            ))
    })
    @PutMapping("/disable/{id}")
    public ResponseEntity<Void> disable(@PathVariable Long id){
        controlApplicationService.disable(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Lista dados de leitura cadastrados",
            description = "Retorna lista com dados de leitura por localização"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dados retornados com sucesso", content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(
                            schema = @Schema(implementation = ControlReadingResponseDTO.class)
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
    @GetMapping("reading-data/{locationId}")
    public ResponseEntity<List<ControlReadingResponseDTO>> getReadingDataByLocationId(@PathVariable Long locationId){
        List<ControlReadingResponseDTO> responseList = controlApplicationService.getReadingDataByLocationId(locationId);
        return ResponseEntity.ok(responseList);
    }

    @Operation(
            summary = "Cadastrar lista de leitura para controle",
            description = "Realiza cadastro dados de leitura ao controle"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Lista de leituras cadastrada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
            )),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
            ))
    })
    @PostMapping("/create/reading-data")
    public ResponseEntity<Void> createReadingData(@RequestBody @Valid List<ControlReadingRequestDTO> controlReadingRequestDTO){
        controlApplicationService.createReadingData(controlReadingRequestDTO);
        return ResponseEntity.noContent().build();
    }
}


