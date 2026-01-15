package com.pontual_telemetria.pontual_monitor_api.domain.service;

import com.pontual_telemetria.pontual_monitor_api.application.mapper.SgmanToRequesterMapper;
import com.pontual_telemetria.pontual_monitor_api.domain.model.customer.Requester;
import com.pontual_telemetria.pontual_monitor_api.domain.repository.RequesterRepository;
import com.pontual_telemetria.pontual_monitor_api.web.dto.sgman.requester.SgmanRequesterDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RequesterDomainService {

    private final RequesterRepository requesterRepository;
    private final SgmanToRequesterMapper sgmanToRequesterMapper;

    @Transactional
    public void updateRequestsBySgman(List<SgmanRequesterDTO> sgmanRequesters) {
        for (SgmanRequesterDTO dto : sgmanRequesters) {

            String celphone = normalizeDigits(dto.getCelular());
            String phone = normalizeDigits(dto.getTelefone());
            String cpf = normalizeDigits(dto.getCpf());
            String cnpj = normalizeDigits(dto.getCnpj());
            String rg = normalizeDigits(dto.getRg());
            String zipCode = normalizeDigits(dto.getCep());

            Optional<Requester> opt = requesterRepository.findByExternalId(dto.getId());

            if (opt.isPresent()) {
                Requester existing = opt.get();

                existing.setName(dto.getNome());
                existing.setCompanyName(dto.getRazaoSocial());
                existing.setCnpj(cnpj);
                existing.setCpf(cpf);
                existing.setRg(rg);
                existing.setCellphone(celphone);
                existing.setPhone(phone);
                existing.setContactName(dto.getNomeContato());
                existing.setEmail(dto.getEmail());
                existing.setAddress(dto.getEndereco());
                existing.setNumber(dto.getNumero());
                existing.setNeighborhood(dto.getBairro());
                existing.setZipCode(zipCode);
                existing.setComplement(dto.getComplemento());
                existing.setState(dto.getUf());
                existing.setCity(dto.getCidade());
                existing.setUpdatedAt(LocalDateTime.now());

                requesterRepository.save(existing);

            } else {
                Requester newRequester = sgmanToRequesterMapper.toEntity(dto);
                newRequester.setCreatedAt(LocalDateTime.now());
                newRequester.setUpdatedAt(LocalDateTime.now());
                requesterRepository.save(newRequester);
            }
        }
    }

    private String normalizeDigits(String value) {
        return value == null ? null : value.replaceAll("\\D", "");
    }
}
