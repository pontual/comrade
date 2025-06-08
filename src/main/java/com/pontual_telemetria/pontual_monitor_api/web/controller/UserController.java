package com.pontual_telemetria.pontual_monitor_api.web.controller;

import com.pontual_telemetria.pontual_monitor_api.application.service.UserApplicationService;
import com.pontual_telemetria.pontual_monitor_api.web.dto.user.UserRequestDTO;
import com.pontual_telemetria.pontual_monitor_api.web.dto.user.UserResponseDTO;
import com.pontual_telemetria.pontual_monitor_api.web.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserApplicationService userApplicationService;

    @Operation(
            summary = "Cadastramento de usuários",
            description = "Recebe informações para cadastramento de nova pessoa/usuário"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cadastro realizado com sucesso", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserResponseDTO.class)
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
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<UserResponseDTO> createUser(@RequestBody @Valid UserRequestDTO userRequest){
       UserResponseDTO response = userApplicationService.createUser(userRequest);
       return response != null ? ResponseEntity.ok(response) : ResponseEntity.noContent().build();
    }
}
