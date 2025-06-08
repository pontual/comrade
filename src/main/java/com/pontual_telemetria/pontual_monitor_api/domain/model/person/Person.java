package com.pontual_telemetria.pontual_monitor_api.domain.model.person;

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
@Table(name = Constants.TABLE_PERSON, schema = Constants.SCHEMA_PERSON)
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 20, unique = true)
    private String document;

    private String email;

    @Column(length = 20)
    private String phone;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updateAt;
}
