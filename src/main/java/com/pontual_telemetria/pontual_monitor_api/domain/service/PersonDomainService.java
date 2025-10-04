package com.pontual_telemetria.pontual_monitor_api.domain.service;

import com.pontual_telemetria.pontual_monitor_api.domain.model.person.Person;
import com.pontual_telemetria.pontual_monitor_api.domain.repository.PersonRepository;
import com.pontual_telemetria.pontual_monitor_api.web.dto.user.PersonDTO;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PersonDomainService {

    private final PersonRepository personRepository;
    private final UserDomainService userDomainService;

    @Transactional
    public void update(PersonDTO personDTO) {
        Person person = getPersonById(personDTO.getId());
        person.setName(personDTO.getName());
        person.setEmail(personDTO.getEmail());
        person.setPhone(personDTO.getPhone());
        personRepository.save(person);
    }

    @Transactional
    public void delete(Long id){
        Person person = getPersonById(id);
        userDomainService.deleteByPersonId(person.getId());
        personRepository.delete(person);
    }

    public Person getPersonById(Long id){
        return personRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pessoa não encontrada"));
    }
}
