package com.pontual_telemetria.pontual_monitor_api.web.controller;

import com.pontual_telemetria.pontual_monitor_api.application.service.AuthApplicationService;
import com.pontual_telemetria.pontual_monitor_api.web.dto.auth.AuthRequestDTO;
import com.pontual_telemetria.pontual_monitor_api.web.dto.auth.AuthResponseDTO;
import com.pontual_telemetria.pontual_monitor_api.web.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthApplicationService authApplicationService;

    @Operation(
            summary = "Autenticaçao de usuário",
            description = "Recebe informações para validaçao de acesso"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário autenticado com sucesso", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = AuthResponseDTO.class)
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
    @PostMapping("/authenticate")
    ResponseEntity<AuthResponseDTO> authenticateUser(@Valid @RequestBody AuthRequestDTO authRequestDTO){
        AuthResponseDTO response = authApplicationService.authenticateUser(authRequestDTO);
        return ResponseEntity.ok(response);
    }
}
