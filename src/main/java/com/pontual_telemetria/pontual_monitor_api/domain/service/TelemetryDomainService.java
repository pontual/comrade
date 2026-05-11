package com.pontual_telemetria.pontual_monitor_api.domain.service;

import com.pontual_telemetria.pontual_monitor_api.web.dto.telemetry.TelemetryResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class TelemetryDomainService {

    @Value("${telemetria.config.multiplier}")
    private BigDecimal multiplier;

    public List<TelemetryResponseDTO> applyFlowRateRules(List<TelemetryResponseDTO> readings) {
        BigDecimal accumulatedHours = BigDecimal.ZERO;
        BigDecimal previousVolume = null;

        for (TelemetryResponseDTO reading : readings) {
            reading.setAdjustedFlowRate(
                    reading.getFlowRate().multiply(multiplier).setScale(3, RoundingMode.HALF_UP)
            );

            BigDecimal intervalHours = new BigDecimal(reading.getDuration())
                    .divide(multiplier, 6, RoundingMode.HALF_UP);
            accumulatedHours = accumulatedHours.add(intervalHours);
            reading.setAdjustedDuration(accumulatedHours.setScale(3, RoundingMode.HALF_UP));

            if (previousVolume == null) {
                reading.setAdjustedVolume(reading.getVolume());
            } else {
                reading.setAdjustedVolume(
                        reading.getVolume().subtract(previousVolume).max(BigDecimal.ZERO)
                );
            }
            previousVolume = reading.getVolume();
        }

        return readings;
    }
}
