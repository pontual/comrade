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
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

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
    ResponseEntity<AuthResponseDTO> authenticateUser(
            @Valid @RequestBody AuthRequestDTO authRequestDTO,
            HttpServletResponse httpResponse
    ){
        var response = authApplicationService.authenticateUser(authRequestDTO);
        ResponseCookie cookie = ResponseCookie.from("accessToken", response.getToken())
                .httpOnly(true)
                .secure(false) //alterar para true em prod
                .sameSite("Strict") //alterar para Lax em subdominio
                .path("/")
                .maxAge(Duration.ofHours(8))
                .build();

        httpResponse.addHeader("Set-Cookie", cookie.toString());

        return ResponseEntity.ok(new AuthResponseDTO(
            true,
                null,
                response.getUsername(),
                response.getRole(),
                response.getPersonName(),
                response.getDocument(),
                response.getEmail(),
                response.getPhone()
        ));
    }

    @Operation(
            summary = "Logout de usuário",
            description = "Realiza logout do usuário e finaliza sessão"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Sessão finalizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
            )),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
            ))
    })
    @PostMapping("/logout")
    public ResponseEntity<Void> logoutUser(HttpServletResponse httpResponse){
        ResponseCookie expiredCookie = ResponseCookie.from("accessToken", "")
                .httpOnly(true)
                .secure(false) //alterar para true em prod
                .sameSite("Strict") //alterar para Lax em subdominio
                .path("/")
                .maxAge(0)
                .build();

        httpResponse.addHeader("Set-Cookie", expiredCookie.toString());
        return ResponseEntity.noContent().build();
    }

}
