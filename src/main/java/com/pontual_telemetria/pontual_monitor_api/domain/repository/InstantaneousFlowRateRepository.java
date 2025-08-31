package com.pontual_telemetria.pontual_monitor_api.domain.repository;

import com.pontual_telemetria.pontual_monitor_api.domain.model.monitoring.InstantaneousFlowRate;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InstantaneousFlowRateRepository extends JpaRepository<InstantaneousFlowRate, Long> {

    @EntityGraph(attributePaths = "location")
    List<InstantaneousFlowRate> findAllByLocation_Id(Long locationId);

    @Query("""
        select i
        from InstantaneousFlowRate i
        where i.externalId = :externalId
          and i.startDate <= :end
          and (i.endDate is null or i.endDate >= :start)
        order by i.startDate desc
    """)
    Optional<InstantaneousFlowRate> findEffectiveForPeriod(
            Long externalId, LocalDateTime start, LocalDateTime end
    );

}
