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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        String startDate = request.getStartDate().toLocalDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        String endDate = request.getEndDate().toLocalDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

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

            Set<LocalDateTime> existingInstants = new HashSet<>(
                    controlReadingDataRepository.findExistingInstantsByTagInRange(
                            intervention,
                            request.getStartDate().toLocalDateTime(),
                            request.getEndDate().toLocalDateTime()
                    )
            );

            List<ControlReadingImportDTO> newReadings = mapper.toImportDTOList(processedReadings).stream()
                    .filter(dto -> !existingInstants.contains(dto.getDtReading()))
                    .toList();

            log.info("[TELEMETRY] leituras de telemetria para o device: {} retornadas com sucesso. Novas: {}, já existentes filtradas: {}.",
                    intervention, newReadings.size(), existingInstants.size());

            return newReadings;
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
}
