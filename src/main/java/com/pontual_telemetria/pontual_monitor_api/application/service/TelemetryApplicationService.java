package com.pontual_telemetria.pontual_monitor_api.application.service;

import com.pontual_telemetria.pontual_monitor_api.application.mapper.TelemetryMapper;
import com.pontual_telemetria.pontual_monitor_api.domain.repository.ControlReadingDataRepository;
import com.pontual_telemetria.pontual_monitor_api.domain.service.TelemetryDomainService;
import com.pontual_telemetria.pontual_monitor_api.infrastructure.client.feign.telemetria.TelemetriaClient;
import com.pontual_telemetria.pontual_monitor_api.infrastructure.client.feign.telemetria.dto.TelemetryAuthRequestDTO;
import com.pontual_telemetria.pontual_monitor_api.infrastructure.client.feign.telemetria.dto.TelemetryAuthResponseDTO;
import com.pontual_telemetria.pontual_monitor_api.web.dto.sismetro.ControlReadingImportDTO;
import com.pontual_telemetria.pontual_monitor_api.web.dto.telemetry.TelemetryDataParser;
import com.pontual_telemetria.pontual_monitor_api.web.dto.telemetry.TelemetryRequestDTO;
import com.pontual_telemetria.pontual_monitor_api.web.dto.telemetry.TelemetryResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TelemetryApplicationService {

    @Value("${telemetria.credentials.user}")
    private String username;

    @Value("${telemetria.credentials.password}")
    private String password;

    @Value("${telemetria.config.token-ttl-minutes}")
    private Integer tokenTtlMinutes;

    private String cachedToken;
    private LocalDateTime tokenExpiry;

    private static final String API_ANA_TELEMETRY = "API Telemetria ANA";

    private final TelemetriaClient client;
    private final TelemetryDomainService domainService;
    private final TelemetryMapper mapper;
    private final ControlReadingDataRepository controlReadingDataRepository;

    public List<ControlReadingImportDTO> getReadings(TelemetryRequestDTO request) {
        String intervention = request.getDeviceIdentifier();
        String startDate = resolveStartDate(request.getStartDate());
        String endDate = resolveEndDate(request.getStartDate());

        try {
            String authorizationHeader = String.format("Bearer %s", getAccessToken());
            String csvData = client.getLeiturasPorIntervencao("text/csv", authorizationHeader, intervention, startDate, endDate);

            log.info("[TELEMETRY] processando leituras de telemetria para o device: {}", intervention);
            List<TelemetryResponseDTO> readingsRawData = TelemetryDataParser.parse(csvData);

            BigDecimal initialAccumulatedHours = controlReadingDataRepository
                    .findLastReadingValueByTagAndCreatedBy(intervention, API_ANA_TELEMETRY)
                    .orElse(BigDecimal.ZERO);
            log.info("[TELEMETRY] horas acumuladas iniciais para {}: {}", intervention, initialAccumulatedHours);

            List<TelemetryResponseDTO> processedReadings = domainService.applyFlowRateRules(readingsRawData, initialAccumulatedHours);
            log.info("[TELEMETRY] leituras de telemetria para o device: {} retornadas com sucesso.", intervention);

            return mapper.toImportDTOList(processedReadings);
        } catch (Exception e) {
            log.error("[TELEMETRY] Erro ao processar leituras de telemetria para a intervencao: {}. ErroMessage: {}", intervention, e.getMessage());
            throw new RuntimeException("Erro ao processar leituras de telemetria", e);
        }
    }

    private String getAccessToken() {
        if (cachedToken == null || LocalDateTime.now().isAfter(tokenExpiry)) {
            TelemetryAuthRequestDTO request = new TelemetryAuthRequestDTO(username, password);
            TelemetryAuthResponseDTO response = client.getAuthToken(request);
            cachedToken = response.getAccessToken();
            tokenExpiry = LocalDateTime.now().plusMinutes(tokenTtlMinutes);
        }
        return cachedToken;
    }

    private String resolveStartDate(LocalDateTime startDate) {
        LocalDateTime ninetyDaysAgo = LocalDateTime.now().minusDays(90);
        LocalDateTime resolved = startDate.isBefore(ninetyDaysAgo) ? ninetyDaysAgo : startDate;
        return resolved.toLocalDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }

    private String resolveEndDate(LocalDateTime startDate) {
        LocalDateTime ninetyDaysAgo = LocalDateTime.now().minusDays(90);
        LocalDateTime startDateResolved = startDate.isBefore(ninetyDaysAgo) ? ninetyDaysAgo : startDate;
        LocalDateTime endDate = startDateResolved.plusDays(90);
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime resolved = endDate.isAfter(today) ? today : endDate;
        return resolved.toLocalDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }
}
