package com.pontual_telemetria.pontual_monitor_api.domain.service;

import com.pontual_telemetria.pontual_monitor_api.web.dto.telemetry.TelemetryResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
public class TelemetryDomainService {

    @Value("${telemetria.config.multiplier}")
    private BigDecimal multiplier;

    // Registros a partir de 12/05/2026 já foram corrigidos na origem; não aplicar correção.
    private static final LocalDate CORRECTION_CUTOFF_DATE = LocalDate.of(2026, 5, 11);

    public List<TelemetryResponseDTO> applyFlowRateRules(List<TelemetryResponseDTO> readings, BigDecimal initialAccumulatedHours) {
        BigDecimal accumulatedHours = initialAccumulatedHours != null ? initialAccumulatedHours : BigDecimal.ZERO;
        BigDecimal previousVolume = null;
        BigDecimal lastPositiveAdjustedFlowRate = null;

        for (TelemetryResponseDTO reading : readings) {
            BigDecimal originalAdjustedFlowRate = reading.getFlowRate()
                    .multiply(multiplier).setScale(3, RoundingMode.HALF_UP);

            if (reading.getFlowRate().compareTo(BigDecimal.ZERO) > 0) {
                lastPositiveAdjustedFlowRate = originalAdjustedFlowRate;
            }

            BigDecimal adjustedVolume = previousVolume == null
                    ? reading.getVolume()
                    : reading.getVolume().subtract(previousVolume).max(BigDecimal.ZERO);
            reading.setAdjustedVolume(adjustedVolume);

            BigDecimal intervalHours;
            boolean shouldCorrect = !reading.getSchedule().toLocalDate().isAfter(CORRECTION_CUTOFF_DATE);

            if (shouldCorrect && adjustedVolume.compareTo(BigDecimal.ZERO) == 0 && reading.getDuration() > 0) {
                // Regra 1: volume não variou mas duração > 0 → zera duração
                reading.setAdjustedFlowRate(originalAdjustedFlowRate);
                intervalHours = BigDecimal.ZERO;

            } else if (shouldCorrect && adjustedVolume.compareTo(BigDecimal.ZERO) > 0
                    && reading.getFlowRate().compareTo(BigDecimal.ZERO) == 0
                    && lastPositiveAdjustedFlowRate != null) {
                // Regra 2: volume aumentou mas vazão = 0 → usa última vazão positiva
                reading.setAdjustedFlowRate(lastPositiveAdjustedFlowRate);
                intervalHours = adjustedVolume.divide(lastPositiveAdjustedFlowRate, 6, RoundingMode.HALF_UP);

            } else {
                reading.setAdjustedFlowRate(originalAdjustedFlowRate);
                intervalHours = new BigDecimal(reading.getDuration())
                        .divide(multiplier, 6, RoundingMode.HALF_UP);
            }

            accumulatedHours = accumulatedHours.add(intervalHours);
            reading.setAdjustedDuration(accumulatedHours.setScale(3, RoundingMode.HALF_UP));
            previousVolume = reading.getVolume();
        }

        return readings;
    }
}
