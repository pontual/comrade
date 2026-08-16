package com.pontual_telemetria.pontual_monitor_api.domain.service;

import com.pontual_telemetria.pontual_monitor_api.domain.model.monitoring.ControlReading;
import com.pontual_telemetria.pontual_monitor_api.web.dto.telemetry.TelemetryResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

            IntervalCalculationResult result = calculateInterval(
                    reading.getSchedule(), originalAdjustedFlowRate, reading.getFlowRate(), reading.getDuration(),
                    adjustedVolume, lastPositiveAdjustedFlowRate);

            reading.setAdjustedFlowRate(result.adjustedFlowRate());
            accumulatedHours = accumulatedHours.add(result.intervalHours());
            reading.setAdjustedDuration(accumulatedHours.setScale(3, RoundingMode.HALF_UP));
            previousVolume = reading.getVolume();
        }

        return readings;
    }

    /**
     * Reaplica as mesmas regras de correção sobre o histórico já persistido de uma tag,
     * em ordem cronológica de dtReading, usando o calculatedVolume (já recalculado a partir
     * do raw_volume) como base para a Regra 2. Registros sem flowRate/duration/calculatedVolume
     * (ex.: importados antes da coluna existir) são pulados e não contribuem para o acumulado.
     */
    public List<ControlReading> recalculateAccumulatedHours(List<ControlReading> orderedReadings) {
        BigDecimal accumulatedHours = BigDecimal.ZERO;
        BigDecimal lastPositiveAdjustedFlowRate = null;
        List<ControlReading> updated = new ArrayList<>();

        for (ControlReading reading : orderedReadings) {
            if (reading.getFlowRate() == null || reading.getDuration() == null || reading.getCalculatedVolume() == null) {
                continue;
            }

            BigDecimal originalAdjustedFlowRate = reading.getFlowRate()
                    .multiply(multiplier).setScale(3, RoundingMode.HALF_UP);

            if (reading.getFlowRate().compareTo(BigDecimal.ZERO) > 0) {
                lastPositiveAdjustedFlowRate = originalAdjustedFlowRate;
            }

            IntervalCalculationResult result = calculateInterval(
                    reading.getDtReading(), originalAdjustedFlowRate, reading.getFlowRate(), reading.getDuration(),
                    reading.getCalculatedVolume(), lastPositiveAdjustedFlowRate);

            accumulatedHours = accumulatedHours.add(result.intervalHours());
            BigDecimal newReadingValue = accumulatedHours.setScale(3, RoundingMode.HALF_UP);
            String newAverage = result.adjustedFlowRate().toPlainString();

            boolean changed = reading.getReadingValue() == null
                    || newReadingValue.compareTo(reading.getReadingValue()) != 0
                    || !newAverage.equals(reading.getAverage());

            if (changed) {
                reading.setReadingValue(newReadingValue);
                reading.setAverage(newAverage);
                reading.setUpdatedAt(LocalDateTime.now());
                updated.add(reading);
            }
        }

        return updated;
    }

    private IntervalCalculationResult calculateInterval(LocalDateTime schedule, BigDecimal originalAdjustedFlowRate,
                                                          BigDecimal flowRate, Integer duration,
                                                          BigDecimal adjustedVolume, BigDecimal lastPositiveAdjustedFlowRate) {
        boolean shouldCorrect = !schedule.toLocalDate().isAfter(CORRECTION_CUTOFF_DATE);

        if (shouldCorrect && adjustedVolume.compareTo(BigDecimal.ZERO) == 0 && duration > 0) {
            // Regra 1: volume não variou mas duração > 0 → zera duração
            return new IntervalCalculationResult(originalAdjustedFlowRate, BigDecimal.ZERO);
        }

        if (shouldCorrect && adjustedVolume.compareTo(BigDecimal.ZERO) > 0
                && flowRate.compareTo(BigDecimal.ZERO) == 0
                && lastPositiveAdjustedFlowRate != null) {
            // Regra 2: volume aumentou mas vazão = 0 → usa última vazão positiva
            BigDecimal intervalHours = adjustedVolume.divide(lastPositiveAdjustedFlowRate, 6, RoundingMode.HALF_UP);
            return new IntervalCalculationResult(lastPositiveAdjustedFlowRate, intervalHours);
        }

        BigDecimal intervalHours = new BigDecimal(duration).divide(multiplier, 6, RoundingMode.HALF_UP);
        return new IntervalCalculationResult(originalAdjustedFlowRate, intervalHours);
    }

    private record IntervalCalculationResult(BigDecimal adjustedFlowRate, BigDecimal intervalHours) {}
}
