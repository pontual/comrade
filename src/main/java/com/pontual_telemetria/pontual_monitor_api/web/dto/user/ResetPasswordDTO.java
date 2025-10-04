package com.pontual_telemetria.pontual_monitor_api.web.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordDTO {
    private String username;
    private String newPassword;
    private String confirmNewPassword;
}
