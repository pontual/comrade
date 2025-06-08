package com.pontual_telemetria.pontual_monitor_api.domain.exception.user;

import com.pontual_telemetria.pontual_monitor_api.domain.exception.PontualMonitorException;
import org.springframework.http.HttpStatus;

public class UserAlreadyExistsException extends PontualMonitorException {
    public UserAlreadyExistsException(String username) {
        super(
                "Usuário já existente",
                "USER_ALREADY_EXISTS",
                HttpStatus.BAD_REQUEST,
                "já existe um cadastro com o nome de usuário informado '" + username + "'"
        );
    }
}
