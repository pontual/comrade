package com.pontual_telemetria.pontual_monitor_api.domain.model.view;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@Embeddable
public class MvMonthlyAggId implements Serializable {

    @Column(name = "external_id")
    private Long externalId;

    @Column(name = "ym")
    private LocalDate ym;
}
