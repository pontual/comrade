package com.pontual_telemetria.pontual_monitor_api.domain.repository;

import com.pontual_telemetria.pontual_monitor_api.domain.model.monitoring.InstantaneousFlowRate;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InstantaneousFlowRateRepository extends JpaRepository<InstantaneousFlowRate, Long> {

    @EntityGraph(attributePaths = "location")
    List<InstantaneousFlowRate> findAllByLocation_Id(Long locationId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update InstantaneousFlowRate r
           set r.endDate = :newStart
         where r.location.id = :locationId
           and r.externalId = :externalId
           and r.endDate is null
    """)
    void closeCurrent(@Param("locationId") Long locationId,
                     @Param("externalId") Long externalId,
                     @Param("newStart") LocalDateTime newStart);
}

