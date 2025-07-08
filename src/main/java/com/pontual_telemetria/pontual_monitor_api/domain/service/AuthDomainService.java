package com.pontual_telemetria.pontual_monitor_api.domain.service;

import com.pontual_telemetria.pontual_monitor_api.domain.exception.PontualMonitorException;
import com.pontual_telemetria.pontual_monitor_api.domain.model.user.AccountUser;
import com.pontual_telemetria.pontual_monitor_api.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthDomainService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AccountUser authenticateUser(String username, String password) {
        AccountUser user = userRepository.findByUsername(username);

        if(user == null) {
            throw new PontualMonitorException("Usuário não encontrado", "USER_NOT_FOUND", HttpStatus.BAD_REQUEST, "O usuário informado não foi encontrado.");
        }

        if(!passwordEncoder.matches(password, user.getPassword())) {
            throw new PontualMonitorException("Senha inválida", "WRONG_PASSWORD", HttpStatus.UNAUTHORIZED, "Senha incorreta.");
        }

        return user;
    }
}
