package com.pontual_telemetria.pontual_monitor_api.web.exception;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@Builder
public class ErrorResponse {

    private String error;
    private String code;
    private HttpStatus status;
    private String message;

}
