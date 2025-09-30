package com.pontual_telemetria.pontual_monitor_api.application.service;

import com.pontual_telemetria.pontual_monitor_api.domain.model.user.AccountUserRequester;
import com.pontual_telemetria.pontual_monitor_api.domain.service.AuthDomainService;
import com.pontual_telemetria.pontual_monitor_api.infrastructure.util.JwtUtil;
import com.pontual_telemetria.pontual_monitor_api.web.dto.auth.AuthRequestDTO;
import com.pontual_telemetria.pontual_monitor_api.web.dto.auth.AuthResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthApplicationService {

    private final AuthDomainService authDomainService;
    private final UserApplicationService userApplicationService;
    private final JwtUtil jwtUtil;

    public AuthResponseDTO authenticateUser(AuthRequestDTO authRequestDTO) {

        log.info("[AUTENTICATE-USER] Iniciada a autenticação do usuário={}", authRequestDTO.getUsername());
        var user = authDomainService.authenticateUser(authRequestDTO.getUsername(), authRequestDTO.getPassword());
        log.info("[AUTENTICATE-USER] Finalizada a autenticação do usuário={}", authRequestDTO.getUsername());

        log.info("[AUTENTICATE-USER] Gerando token de acesso");
        String token = jwtUtil.generateAccessToken(user.getUsername(), List.of(user.getRole()));
        log.info("[AUTENTICATE-USER] Token de acesso gerado com sucesso");

        List<AccountUserRequester> data = userApplicationService.getAccountRequestersById(user.getId());

        List<Integer> vinculatedRequesters = new ArrayList<>();

        data.forEach(r -> {
            Integer requester = r.getRequesterId();
            vinculatedRequesters.add(requester);
        });

        return new AuthResponseDTO(
                user.getId(),
                true,
                token,
                user.getUsername(),
                user.getRole(),
                user.getPerson().getName(),
                user.getPerson().getDocument(),
                user.getPerson().getEmail(),
                user.getPerson().getPhone(),
                vinculatedRequesters
        );
    }

    public String refreshToken(String username, List<String> roles) {
        log.info("[REFRESH-TOKEN] Gerando token de renovação de acesso");
        String refreshToken = jwtUtil.generateTokenFromRefreshToken(username, roles);
        log.info("[REFRESH-TOKEN] Token de renovação de acesso gerado com sucesso");
        return refreshToken;
    }
}
