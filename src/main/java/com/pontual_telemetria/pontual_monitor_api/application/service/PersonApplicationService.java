package com.pontual_telemetria.pontual_monitor_api.application.service;

import com.pontual_telemetria.pontual_monitor_api.domain.service.PersonDomainService;
import com.pontual_telemetria.pontual_monitor_api.web.dto.user.PersonDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PersonApplicationService {

    private final PersonDomainService personDomainService;
    private final PersonDataFetcher personDataFetcher;

    public PersonDTO getByDocument(String document){
        log.info("Recuperando dados da pessoa documento={}", document);
        PersonDTO person = personDataFetcher.getByDocument(document);
        log.info("Dados da pessoa recuperados com sucesso documento={}", document);
        return person;
    }

    @Transactional
    public void update(PersonDTO personDTO) {
        log.info("Iniciando atualização de dados da pessoa nome={}", personDTO.getName());
        personDomainService.update(personDTO);
        log.info("Finaliazada atualização de dados da pessoa nome={}", personDTO.getName());
    }

    @Transactional
    public void delete(Long id) {
        log.info("Iniciando exclusão de dados da pessoa id={}", id);
        personDomainService.delete(id);
        log.info("Finalizada exclusão de dados da pessoa id={}", id);
    }
}
