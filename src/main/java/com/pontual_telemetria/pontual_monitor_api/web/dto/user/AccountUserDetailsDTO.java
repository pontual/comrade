package com.pontual_telemetria.pontual_monitor_api.web.dto.user;

import com.pontual_telemetria.pontual_monitor_api.domain.model.person.Person;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountUserDetailsDTO {
    private Long id;
    private String username;
    private String role;
    private Boolean enabled;
    private Person person;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
