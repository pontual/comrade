package com.pontual_telemetria.pontual_monitor_api.web.controller;

import com.pontual_telemetria.pontual_monitor_api.application.service.AuthApplicationService;
import com.pontual_telemetria.pontual_monitor_api.infrastructure.util.CookieUtil;
import com.pontual_telemetria.pontual_monitor_api.infrastructure.util.JwtUtil;
import com.pontual_telemetria.pontual_monitor_api.web.dto.auth.AuthRequestDTO;
import com.pontual_telemetria.pontual_monitor_api.web.dto.auth.AuthResponseDTO;
import com.pontual_telemetria.pontual_monitor_api.web.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthApplicationService authApplicationService;
    private final CookieUtil cookieUtil;
    private final JwtUtil jwtUtil;

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
        String refreshToken = authApplicationService.refreshToken(response.getUsername(), List.of(response.getRole()));
        ResponseCookie accessCookie = cookieUtil.createCookie(CookieUtil.ACCESS_TOKEN_COOKIE, response.getToken(), Duration.ofMinutes(15));

        ResponseCookie refreshCookie;

        if(authRequestDTO.isRememberMe()) {
            refreshCookie = cookieUtil.createCookie(CookieUtil.REFRESH_TOKEN_COOKIE, refreshToken, Duration.ofDays(15));
        } else {
            refreshCookie = cookieUtil.createSessionCookie(CookieUtil.REFRESH_TOKEN_COOKIE, refreshToken);
        }

        CookieUtil.attachCookies(httpResponse, accessCookie, refreshCookie);

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
            summary = "Renovação de autenticação de usuário",
            description = "Renova token de acesso"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Token renovado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
            )),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
            ))
    })
    @PostMapping("/refresh-token")
    public ResponseEntity<Void>  refreshToken(HttpServletRequest request, HttpServletResponse response){
        String refreshToken = cookieUtil.getCookieValue(request, CookieUtil.REFRESH_TOKEN_COOKIE);

        if(refreshToken != null && jwtUtil.isTokenValid(refreshToken)) {
            String username  = jwtUtil.extractUsername(refreshToken);
            List<String> roles = jwtUtil.extractRoles(refreshToken);

            String newAccessToken = jwtUtil.generateTokenFromRefreshToken(username, roles);

            ResponseCookie accessCookie = cookieUtil.createCookie(CookieUtil.ACCESS_TOKEN_COOKIE, newAccessToken, Duration.ofMinutes(15));
            CookieUtil.attachCookies(response, accessCookie);

            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
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
    public ResponseEntity<Void> logoutUser(HttpServletResponse httpResponse) {
        ResponseCookie expiredAccessToken = cookieUtil.expireCookie(CookieUtil.ACCESS_TOKEN_COOKIE);
        ResponseCookie expiredRefreshToken = cookieUtil.expireCookie(CookieUtil.REFRESH_TOKEN_COOKIE);
        CookieUtil.attachCookies(httpResponse, expiredAccessToken, expiredRefreshToken);
        return ResponseEntity.noContent().build();
    }
}
