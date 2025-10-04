package com.pontual_telemetria.pontual_monitor_api.domain.model.customer;

import com.pontual_telemetria.pontual_monitor_api.infrastructure.util.Constants;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = Constants.TABLE_REQUESTER, schema = Constants.SCHEMA_CUSTOMER)
public class Requester {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id")
    private Long externalId;

    private String name;

    @Column(name = "company_name")
    private String companyName;

    @Column(length = 14)
    private String cnpj;

    @Column(length = 11)
    private String cpf;

    @Column(length = 20)
    private String rg;

    @Column(length = 11)
    private String cellphone;

    @Column(length = 11)
    private String phone;

    @Column(name = "contact_name")
    private String contactName;

    private String email;

    @Column(length = 100)
    private String address;

    @Column(length = 10)
    private String number;

    @Column(length = 60)
    private String neighborhood;

    @Column(name = "zip_code", length = 8)
    private String zipCode;

    @Column(length = 30)
    private String complement;

    private Integer state;

    private Integer city;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
