package com.pontual_telemetria.pontual_monitor_api.web.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

@Data
public class UserRequestDTO {

    @NotBlank(message = "O nome é obrigatório.")
    private String name;

    @NotBlank(message = "O documento é obrigatório.")
    private String document;

    @Email(message = "E-mail inválido.")
    private String email;

    private String phone;

    @NotNull(message = "Informe se deve criar conta de usuário.")
    private Boolean isCreateAccountUser;

    private String username;

    @ToString.Exclude
    private String password;

    private String role;

    private boolean enabled;
}
