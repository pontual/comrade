package com.pontual_telemetria.pontual_monitor_api.domain.repository;

import com.pontual_telemetria.pontual_monitor_api.domain.model.monitoring.InstantaneousFlowRate;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InstantaneousFlowRateRepository extends JpaRepository<InstantaneousFlowRate, Long> {

    @EntityGraph(attributePaths = "location")
    List<InstantaneousFlowRate> findAllByLocation_Id(Long locationId);
}
