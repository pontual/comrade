package com.pontual_telemetria.pontual_monitor_api.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequesterDTO {
    private Long id;
    private Long externalId;
    private String name;
    private String companyName;
    private String cnpj;
    private String cpf;
    private String rg;
    private String cellphone;
    private String phone;
    private String contactName;
    private String email;
    private String address;
    private String number;
    private String neighborhood;
    private String zipCode;
    private String complement;
    private Integer state;
    private Integer city;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
