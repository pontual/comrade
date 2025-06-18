package com.pontual_telemetria.pontual_monitor_api.domain.exception.sgman;

import com.pontual_telemetria.pontual_monitor_api.domain.exception.PontualMonitorException;
import org.springframework.http.HttpStatus;

public class SgmanException extends PontualMonitorException {
    public SgmanException(String message) {
        super(
                "Erro consulta SGMAN",
                "SGMAN_SERVICE_ERROR",
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro ao realizar consulta SGMAN: '" + message + "'"
        );
    }
}
