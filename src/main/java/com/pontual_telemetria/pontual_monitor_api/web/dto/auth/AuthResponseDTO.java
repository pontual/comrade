package com.pontual_telemetria.pontual_monitor_api.web.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
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
    private List<Integer> vinculatedRequesters;
}
