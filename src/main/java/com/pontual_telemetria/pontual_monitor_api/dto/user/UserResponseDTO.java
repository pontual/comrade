package com.pontual_telemetria.pontual_monitor_api.dto.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponseDTO {
    private Long id;
    private String username;
    private String role;
    private String personName;
    private String document;
    private String email;
    private String phone;
}
