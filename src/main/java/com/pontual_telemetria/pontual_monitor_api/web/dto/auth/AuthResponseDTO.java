package com.pontual_telemetria.pontual_monitor_api.web.dto.auth;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@NotNull
public class AuthResponseDTO {
    private Long id;
    private Boolean isValidLogin;
    private String token;
    private String username;
    private String role;
    private String personName;
    private String document;
    private String email;
    private String phone;
}
