package com.pontual_telemetria.pontual_monitor_api.application.mapper;

import com.pontual_telemetria.pontual_monitor_api.domain.model.customer.Requester;
import com.pontual_telemetria.pontual_monitor_api.web.dto.sgman.requester.SgmanRequesterDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Component
public class SgmanToRequesterMapper {

    public Requester toEntity(SgmanRequesterDTO dto) {
         return Requester.builder()
                    .externalId(dto.getId())
                    .name(dto.getNome())
                    .companyName(dto.getRazaoSocial())
                    .cnpj(clean(dto.getCnpj()))
                    .cpf(clean(dto.getCpf()))
                    .rg(dto.getRg())
                    .cellphone(clean(dto.getCelular()))
                    .phone(clean(dto.getTelefone()))
                    .contactName(dto.getNomeContato())
                    .email(dto.getEmail())
                    .address(dto.getEndereco())
                    .number(dto.getNumero())
                    .neighborhood(dto.getBairro())
                    .zipCode(clean(dto.getCep()))
                    .complement(dto.getComplemento())
                    .state(dto.getUf())
                    .city(dto.getCidade())
                    .createdAt(parseDate(dto.getDataCadastro()))
                    .updatedAt(parseDate(dto.getDataAlteracao()))
                    .build();
    }

    private String clean(String value) {
        return value != null ? value.replaceAll("\\D", "") : null;
    }

    private LocalDateTime parseDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) return null;
        try {
            return LocalDateTime.parse(dateStr);
        } catch (DateTimeParseException e) {
            return LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        }
    }

}
