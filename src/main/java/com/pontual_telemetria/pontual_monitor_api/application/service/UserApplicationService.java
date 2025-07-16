package com.pontual_telemetria.pontual_monitor_api.application.service;

import com.pontual_telemetria.pontual_monitor_api.application.mapper.PersonMapper;
import com.pontual_telemetria.pontual_monitor_api.application.mapper.UserResponseMapper;
import com.pontual_telemetria.pontual_monitor_api.domain.model.user.AccountUser;
import com.pontual_telemetria.pontual_monitor_api.domain.model.person.Person;
import com.pontual_telemetria.pontual_monitor_api.domain.repository.PersonRepository;
import com.pontual_telemetria.pontual_monitor_api.domain.repository.UserRepository;
import com.pontual_telemetria.pontual_monitor_api.domain.service.UserDomainService;
import com.pontual_telemetria.pontual_monitor_api.web.dto.user.*;
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
        log.info("[GET-ACCOUNT-USERS] Buscando usuário cadastrados");
        Page<AccountUserDetailsDTO> users = accountUserFetcher.getAllPaginated(page, size);
        log.info("[GET-ACCOUNT-USERS] Dados retornados com sucesso");
        return users;
    }

    public AccountUserDetailsDTO getByCPF(String cpf) {
        log.info("[GET-PERSON] Buscando usuário por cpf={}", cpf);
        AccountUserDetailsDTO user = accountUserFetcher.getByCPF(cpf);
        log.info("[GET-PERSON] Busca realizada com sucesso para o usuário cpf={}", cpf);
        return user;
    }

    @Transactional
    public UserResponseDTO create(UserRequestDTO userRequest) {

        Person person = personMapper.toEntity(userRequest);
        userDomainService.verifyIfPersonExists(userRequest.getDocument());
        log.info("[CREATE-PERSON] Criando pessoa: {}", person);
        personRepository.save(person);
        log.info("[CREATE-PERSON] Pessoa criada com sucesso: {}", person);

        if(Boolean.TRUE.equals(userRequest.getIsCreateAccountUser())) {
            userDomainService.verifyIfUsernameExists(userRequest.getUsername());
            AccountUser accountUser = userDomainService.createAccountUser(userRequest, person);
            log.info("[CREATE-ACCOUNT-USER] Criando usuário: username={}, role={}", accountUser.getUsername(), accountUser.getRole());
            userRepository.save(accountUser);
            log.info("[CREATE-ACCOUNT-USER] Usuário criado: username={}, role={}", accountUser.getUsername(), accountUser.getRole());
        }

        log.info("[CREATE-ACCOUNT_USER] Pessoa criada com sucesso: id={}, document={}, name={}", person.getId(), person.getDocument(), person.getName());
        return userResponseMapper.toDTO(person, userRequest.getUsername(), userRequest.getRole(), userRequest.isEnabled());
    }

    @Transactional
    public void resetPassword(ResetPasswordDTO resetPasswordDTO) {
        log.info("[RESET-PASSWORD] Iniciado processo de alteração de senha para o usuário={}" , resetPasswordDTO.getUsername());
        userDomainService.resetPassword(resetPasswordDTO);
        log.info("[RESET-PASSWORD] Finalizado o processo de alteração de senha para o usuário={}" , resetPasswordDTO.getUsername());
    }

    @Transactional
    public void update(AccountUserDTO accountUserDTO) {
        log.info("[UPDATE-ACCOUNT-USER] Iniciada a atualização de dados do usuário username={}", accountUserDTO.getUsername());
        userDomainService.update(accountUserDTO);
        log.info("[UPDATE-ACCOUNT-USER] Dados do usuário atualizados com sucesso username={}", accountUserDTO.getUsername());
    }

    @Transactional
    public void delete(Long id) {
        log.info("[DELETE-ACCOUNT-USER] Deletando usuário id={}", id);
        userDomainService.delete(id);
        log.info("[DELETE-ACCOUNT-USER] Dados de usuário deletados com sucesso id={}", id);
    }
}
