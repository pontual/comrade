package com.pontual_telemetria.pontual_monitor_api.web.exception;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorResponse {

    private String error;
    private String code;
    private int status;
    private String message;

}
