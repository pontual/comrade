package com.pontual_telemetria.pontual_monitor_api.domain.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class PontualMonitorException extends RuntimeException {

    private final String code;
    private final HttpStatus status;
    private final String error;

    public PontualMonitorException(String error, String code, HttpStatus status, String message) {
        super(message);
        this.code = code;
        this.status = status;
        this.error = error;
    }
}
