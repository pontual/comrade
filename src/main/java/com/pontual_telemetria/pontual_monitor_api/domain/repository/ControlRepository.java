package com.pontual_telemetria.pontual_monitor_api.domain.repository;

import com.pontual_telemetria.pontual_monitor_api.domain.model.monitoring.Control;
import feign.Param;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ControlRepository extends JpaRepository<Control, Long> {

    @EntityGraph(attributePaths = {"readings"})
    @Query("SELECT c FROM Control c WHERE c.location.externalId = :externalId")
    List<Control> findAllWithReadings(@Param("externalId") Long externalId);
}

