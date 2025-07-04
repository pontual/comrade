package com.pontual_telemetria.pontual_monitor_api.application.service;

import com.pontual_telemetria.pontual_monitor_api.application.mapper.PersonMapper;
import com.pontual_telemetria.pontual_monitor_api.application.mapper.UserResponseMapper;
import com.pontual_telemetria.pontual_monitor_api.domain.model.account_user.AccountUser;
import com.pontual_telemetria.pontual_monitor_api.domain.model.person.Person;
import com.pontual_telemetria.pontual_monitor_api.domain.repository.PersonRepository;
import com.pontual_telemetria.pontual_monitor_api.domain.repository.UserRepository;
import com.pontual_telemetria.pontual_monitor_api.domain.service.UserDomainService;
import com.pontual_telemetria.pontual_monitor_api.web.dto.user.AccountUserDTO;
import com.pontual_telemetria.pontual_monitor_api.web.dto.user.AccountUserDetailsDTO;
import com.pontual_telemetria.pontual_monitor_api.web.dto.user.UserRequestDTO;
import com.pontual_telemetria.pontual_monitor_api.web.dto.user.UserResponseDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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
    private final AccountUserFetcher accountUserFetcher;

    public Page<AccountUserDetailsDTO> getAllPaginated(int page, int size) {
        log.info("Buscando usuário cadastrados");
        Page<AccountUserDetailsDTO> users = accountUserFetcher.getAllPaginated(page, size);
        log.info("Dados retornados com sucesso");
        return users;
    }

    public AccountUserDetailsDTO getByCPF(String cpf) {
        log.info("Buscando usuário por cpf={}", cpf);
        AccountUserDetailsDTO user = accountUserFetcher.getByCPF(cpf);
        log.info("Busca realizada com sucesso para o usuário cpf={}", cpf);
        return user;
    }

    @Transactional
    public UserResponseDTO create(UserRequestDTO userRequest) {

        Person person = personMapper.toEntity(userRequest);
        userDomainService.verifyIfPersonExists(userRequest.getDocument());
        log.info("Criando pessoa: {}", person);
        personRepository.save(person);
        log.info("Pessoa criada: {}", person);

        if(Boolean.TRUE.equals(userRequest.getIsCreateAccountUser())) {
            userDomainService.verifyIfUsernameExists(userRequest.getUsername());
            AccountUser accountUser = userDomainService.createAccountUser(userRequest, person);
            log.info("Criando usuário: username={}, role={}", accountUser.getUsername(), accountUser.getRole());
            userRepository.save(accountUser);
            log.info("Usuário criado: username={}, role={}", accountUser.getUsername(), accountUser.getRole());
        }

        log.info("Pessoa criada com sucesso: id={}, document={}, name={}", person.getId(), person.getDocument(), person.getName());
        return userResponseMapper.toDTO(person, userRequest.getUsername(), userRequest.getRole(), userRequest.isEnabled());
    }

    @Transactional
    public void update(AccountUserDTO accountUserDTO) {
        log.info("Iniciada a atualização de dados do usuário username={}", accountUserDTO.getUsername());
        userDomainService.update(accountUserDTO);
        log.info("Dados do usuário atualizados com sucesso username={}", accountUserDTO.getUsername());
    }

    @Transactional
    public void delete(Long id) {
        log.info("Deletando usuário id={}", id);
        userDomainService.delete(id);
        log.info("Dados de usuário deletados com sucesso id={}", id);
    }
}
