package com.pontual_telemetria.pontual_monitor_api.web.dto.auth;

import lombok.Data;

@Data
public class AuthRequestDTO {
    private String username;
    private String password;
}
