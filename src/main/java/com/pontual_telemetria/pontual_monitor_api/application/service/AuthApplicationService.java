package com.pontual_telemetria.pontual_monitor_api.application.service;

import com.pontual_telemetria.pontual_monitor_api.domain.service.AuthDomainService;
import com.pontual_telemetria.pontual_monitor_api.infrastructure.util.JwtUtil;
import com.pontual_telemetria.pontual_monitor_api.web.dto.auth.AuthRequestDTO;
import com.pontual_telemetria.pontual_monitor_api.web.dto.auth.AuthResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthApplicationService {

    private final AuthDomainService authDomainService;
    private final JwtUtil jwtUtil;

    public AuthResponseDTO authenticateUser(AuthRequestDTO authRequestDTO) {

        log.info("Iniciada a autenticação do usuário={}", authRequestDTO.getUsername());
        var user = authDomainService.authenticateUser(authRequestDTO.getUsername(), authRequestDTO.getPassword());
        log.info("Finalizada a autenticação do usuário={}", authRequestDTO.getUsername());

        log.info("Gerando token de acesso");
        String token = jwtUtil.generateAccessToken(user.getUsername(), List.of(user.getRole()));
        log.info("Token de acesso gerado com sucesso");

        return new AuthResponseDTO(
                user.getId(),
                true,
                token,
                user.getUsername(),
                user.getRole(),
                user.getPerson().getName(),
                user.getPerson().getDocument(),
                user.getPerson().getEmail(),
                user.getPerson().getPhone()
        );
    }

    public String refreshToken(String username, List<String> roles) {
        log.info("Gerando token de renovação de acesso");
        String refreshToken = jwtUtil.generateTokenFromRefreshToken(username, roles);
        log.info("Token de renovação de acesso gerado com sucesso");
        return refreshToken;
    }
}
