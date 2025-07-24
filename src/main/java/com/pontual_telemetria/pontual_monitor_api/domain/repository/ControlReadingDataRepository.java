package com.pontual_telemetria.pontual_monitor_api.domain.repository;

import com.pontual_telemetria.pontual_monitor_api.domain.model.monitoring.ControlReading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ControlReadingDataRepository extends JpaRepository<ControlReading, Long> {
    List<ControlReading> findAllByLocationId(Long locationId);
}
