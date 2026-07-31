package com.pontual_telemetria.pontual_monitor_api.domain.model.monitoring;

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
@Table(
        name = Constants.TABLE_DEVICE,
        schema = Constants.SCHEMA_MONITORING,
        indexes = {
                @Index(name = "idx_device_identifier", columnList = "identifier"),
        }
)
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100)
    private String brand;

    @Column(length = 100, nullable = false)
    private String identifier;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private Boolean active;

    @Column(name = "linked_patrimony")
    private String linkedPatrimony;

    @Column(name = "is_fonte_dados_api_ana", nullable = false)
    private Boolean isFonteDadosApiAna;
}
