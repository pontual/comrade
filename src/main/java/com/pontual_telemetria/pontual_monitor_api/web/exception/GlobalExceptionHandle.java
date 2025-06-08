package com.pontual_telemetria.pontual_monitor_api.web.exception;

import com.pontual_telemetria.pontual_monitor_api.domain.exception.PontualMonitorException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class GlobalExceptionHandle {

    @ExceptionHandler(PontualMonitorException.class)
    public ResponseEntity<ErrorResponse> handlePontualMonitorException(PontualMonitorException ex) {
        ErrorResponse response = ErrorResponse.builder()
                .error(ex.getError())
                .code(ex.getCode())
                .status(ex.getStatus())
                .message(ex.getMessage())
                .build();

        return ResponseEntity.status(ex.getStatus()).body(response);
    }
}
