package com.pontual_telemetria.pontual_monitor_api.domain.service;

import com.pontual_telemetria.pontual_monitor_api.domain.model.monitoring.DailyOperationHoursOverride;
import com.pontual_telemetria.pontual_monitor_api.domain.repository.DailyOperationHoursOverrideRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class DailyHoursOverrideDomainService {

    private final DailyOperationHoursOverrideRepository repository;

    @Transactional
    public DailyOperationHoursOverride upsert(Long externalId, LocalDate day, BigDecimal hours, String user) {
        return repository.findByExternalIdAndDay(externalId, day)
                .map(entity -> {
                    entity.setDailyHoursOverride(hours);
                    entity.setUpdatedBy(user);
                    return repository.save(entity);
                })
                .orElseGet(() -> repository.save(
                        DailyOperationHoursOverride.builder()
                                .externalId(externalId)
                                .day(day)
                                .dailyHoursOverride(hours)
                                .updatedBy(user)
                                .build()
                ));
    }

    @Transactional
    public void delete(Long externalId, LocalDate day) {
        repository.deleteByExternalIdAndDay(externalId, day);
    }
}
