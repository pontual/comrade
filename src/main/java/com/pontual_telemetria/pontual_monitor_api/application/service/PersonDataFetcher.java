package com.pontual_telemetria.pontual_monitor_api.application.service;

import com.pontual_telemetria.pontual_monitor_api.application.mapper.PersonMapper;
import com.pontual_telemetria.pontual_monitor_api.domain.model.person.Person;
import com.pontual_telemetria.pontual_monitor_api.domain.repository.PersonRepository;
import com.pontual_telemetria.pontual_monitor_api.web.dto.user.PersonDTO;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PersonDataFetcher {

    private final PersonRepository personRepository;
    private final PersonMapper personMapper;

    public PersonDTO getByDocument(String documento) {
        Person person = personRepository.getPersonByDocument(documento);
        if(person == null){
            throw new EntityNotFoundException("Pessoa não encontrada");
        }
        return personMapper.toDto(person);
    }
}
