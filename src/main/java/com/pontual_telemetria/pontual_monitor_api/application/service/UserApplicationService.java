package com.pontual_telemetria.pontual_monitor_api.application.service;

import com.pontual_telemetria.pontual_monitor_api.application.mapper.PersonMapper;
import com.pontual_telemetria.pontual_monitor_api.application.mapper.UserResponseMapper;
import com.pontual_telemetria.pontual_monitor_api.domain.model.account_user.AccountUser;
import com.pontual_telemetria.pontual_monitor_api.domain.model.person.Person;
import com.pontual_telemetria.pontual_monitor_api.domain.repository.PersonRepository;
import com.pontual_telemetria.pontual_monitor_api.domain.repository.UserRepository;
import com.pontual_telemetria.pontual_monitor_api.domain.service.UserDomainService;
import com.pontual_telemetria.pontual_monitor_api.web.dto.user.UserRequestDTO;
import com.pontual_telemetria.pontual_monitor_api.web.dto.user.UserResponseDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserApplicationService {

    private final PersonRepository personRepository;
    private final UserRepository userRepository;
    private final UserDomainService userDomainService;
    private final PersonMapper personMapper;
    private final UserResponseMapper userResponseMapper;

    @Transactional
    public UserResponseDTO createUser(UserRequestDTO userRequest) {

        Person person = personMapper.toEntity(userRequest);

        log.info("Creating person: {}", person);
        personRepository.save(person);
        log.info("Person created: {}", person);

        if(Boolean.TRUE.equals(userRequest.getIsCreateAccountUser())) {
            userDomainService.verifyIfUsernameExists(userRequest.getUsername());
            AccountUser accountUser = userDomainService.createAccountUser(userRequest, person);
            log.info("Creating user: username={}, role={}", accountUser.getUsername(), accountUser.getRole());
            userRepository.save(accountUser);
            log.info("User created: username={}, role={}", accountUser.getUsername(), accountUser.getRole());
        }

        log.info("person success created: id={}, document={}, name={}", person.getId(), person.getDocument(), person.getName());
        return userResponseMapper.toDTO(person, userRequest.getUsername(), userRequest.getRole());
    }
}
