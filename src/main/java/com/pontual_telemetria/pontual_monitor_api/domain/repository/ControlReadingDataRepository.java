package com.pontual_telemetria.pontual_monitor_api.domain.repository;

import com.pontual_telemetria.pontual_monitor_api.domain.model.monitoring.ControlReading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ControlReadingDataRepository extends JpaRepository<ControlReading, Long> {
    List<ControlReading> findAllByLocationId(Long locationId);
    List<ControlReading> findAllByExternalId(Long externalId);

    @Query("""
        select r.dtReading
        from ControlReading r
        where r.location.id = :locationId
            and r.dtReading between :start and :end
        """)
    List<LocalDateTime> findExistingInstantsInRangeByLocation(
            @Param("locationId") Integer locationId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}
