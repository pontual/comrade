package com.pontual_telemetria.pontual_monitor_api.application.mapper;

import com.pontual_telemetria.pontual_monitor_api.domain.model.customer.Location;
import com.pontual_telemetria.pontual_monitor_api.web.dto.sgman.location.SgmanLocationDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Slf4j
@Component
public class SgmanToLocationMapper {
    public Location toEntity(SgmanLocationDTO dto){
       return Location.builder()
                .externalId(dto.getId())
                .description(dto.getDescricao())
                .requesterId(dto.getIdSolicitante())
                .locationId(dto.getIdLocalizacao())
                .locationName(dto.getLocalizacao())
                .categoryId(dto.getIdCategoria())
                .category(dto.getCategoria())
                .typeTechId(dto.getIdTipoTec())
                .typeTech(dto.getTipoTec())
                .observation(dto.getObservacao())
                .brandId(dto.getIdMarca())
                .brand(dto.getMarca())
                .modelId(dto.getIdModelo())
                .model(dto.getModelo())
                .serial(dto.getSerial())
                .patrimony(dto.getPatrimonio())
                .tag(dto.getTag())
                .dataMatrix(dto.getDataMatrix())
                .details(dto.getDetalhes())
                .status(dto.getStatus())
                .includedAt(parseDate(dto.getDataHoraInclusao()))
                .updatedAt(parseDate(dto.getDataHoraAlteracao()))
                .guaranteeUntil(parseDate(dto.getDataGarantia()))
                .situation(dto.getSituacao())
                .situationId(dto.getIdSituacao())
                .build();
    }

    private static final List<DateTimeFormatter> FORMATTERS = List.of(
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss") // ISO com 'T' também
    );

    private LocalDateTime parseDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) return null;

        for (DateTimeFormatter formatter : FORMATTERS) {
            try {
                return LocalDateTime.parse(dateStr, formatter);
            } catch (DateTimeParseException ignored) {
                log.error("Erro ao aplicar formatador erro: {}", ignored.getErrorIndex());
            }
        }

        log.error("Erro ao parsear data: {}", dateStr);
        return null;
    }

}
