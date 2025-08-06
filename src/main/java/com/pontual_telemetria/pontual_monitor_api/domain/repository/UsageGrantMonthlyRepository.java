package com.pontual_telemetria.pontual_monitor_api.domain.repository;

import com.pontual_telemetria.pontual_monitor_api.domain.model.regulatory.UsageGrantMonthly;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface UsageGrantMonthlyRepository extends JpaRepository<UsageGrantMonthly, Long> {

    @Modifying
    @Query("""
        UPDATE UsageGrantMonthly u
        SET u.flowRate = :flowRate,
            u.hoursDay = :hoursDay,
            u.daysMonth = :daysMonth,
            u.maximumVolume = :maximumVolume
        WHERE u.id = :id
    """)
    void updateMonthly(
            @Param("id") Long id,
            @Param("flowRate") BigDecimal flowRate,
            @Param("hoursDay") BigDecimal hoursDay,
            @Param("daysMonth") BigDecimal daysMonth,
            @Param("maximumVolume") BigDecimal maximumVolume
    );
}
