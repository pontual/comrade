package com.pontual_telemetria.pontual_monitor_api.web.exception;

import com.pontual_telemetria.pontual_monitor_api.domain.exception.PontualMonitorException;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandle {

    @ExceptionHandler(PontualMonitorException.class)
    public ResponseEntity<ErrorResponse> handlePontualMonitorException(PontualMonitorException ex) {
        log.error("PontualMonitorException - {}: {}", ex.getCode(), ex.getMessage(), ex);
        ErrorResponse response = ErrorResponse.builder()
                .error(ex.getError())
                .code(ex.getCode())
                .status(ex.getStatus().value())
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(ex.getStatus()).body(response);
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ErrorResponse> handleFeignException(FeignException ex) {
        HttpStatus status = HttpStatus.resolve(ex.status());
        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        String responseBody = ex.contentUTF8();

        log.error("FeignException - status {}: {}", status, responseBody, ex);

        return ResponseEntity.status(status)
                .body(ErrorResponse.builder()
                        .error("Erro de integração")
                        .code("INTEGRATION_ERROR")
                        .status(status.value())
                        .message(responseBody != null && !responseBody.isBlank() ? responseBody : "Erro desconhecido ao realizar integração")
                        .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Exception não tratada: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.builder()
                        .error("Erro interno no servidor")
                        .code("UNEXPECTED_ERROR")
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .message(ex.getMessage())
                        .build()
                );
    }
}
