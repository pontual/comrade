package com.pontual_telemetria.pontual_monitor_api.domain.repository;

import com.pontual_telemetria.pontual_monitor_api.domain.model.monitoring.DailyOperationHoursOverride;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface DailyOperationHoursOverrideRepository extends JpaRepository<DailyOperationHoursOverride, Long> {
    Optional<DailyOperationHoursOverride> findByExternalIdAndDay(Long externalId, LocalDate day);
    void deleteByExternalIdAndDay(Long externalId, LocalDate day);
    boolean existsByExternalIdAndDay(Long externalId, LocalDate day);
}
