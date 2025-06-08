package com.pontual_telemetria.pontual_monitor_api.domain.exception.person;

import com.pontual_telemetria.pontual_monitor_api.domain.exception.PontualMonitorException;
import org.springframework.http.HttpStatus;

public class PersonAlreadyExistsException extends PontualMonitorException {
    public PersonAlreadyExistsException(String document) {
        super(
                "Número de documento já cadastrado",
                "PERSON_DOCUMENT_ALREADY_EXISTS",
                HttpStatus.BAD_REQUEST,
                "já existe um cadastro com o número de documento informado '" + document + "'"
        );
    }
}
