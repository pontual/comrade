package com.pontual_telemetria.pontual_monitor_api.web.controller;

import com.pontual_telemetria.pontual_monitor_api.application.service.DeviceApplicationService;
import com.pontual_telemetria.pontual_monitor_api.web.dto.monitoring.DeviceDTO;
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
@RequestMapping("/device")
public class DeviceController {

    private final DeviceApplicationService deviceApplicationService;

    @Operation(
            summary = "Lista dispositivos cadastrados",
            description = "Retorna lista com dispositivos cadastrados"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dispositivos retornados com sucesso", content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(
                            schema = @Schema(implementation = DeviceDTO.class)
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
    @GetMapping()
    public ResponseEntity<List<DeviceDTO>> getDevices(){
        List<DeviceDTO> response = deviceApplicationService.getDevices();
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Cadastrar dispositivo",
            description = "Realiza cadastro de novo dispositivo"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Dispositivo cadastrado com sucesso"),
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
    public ResponseEntity<Void> create(@RequestBody @Valid DeviceDTO deviceDTO){
        deviceApplicationService.create(deviceDTO);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Deleta dispositivo",
            description = "Apaga dados dispositivo por id"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Dispositivo excluído com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
            )),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
            ))
    })
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        deviceApplicationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
