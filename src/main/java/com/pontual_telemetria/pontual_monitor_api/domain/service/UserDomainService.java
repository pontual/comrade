package com.pontual_telemetria.pontual_monitor_api.domain.service;

import com.pontual_telemetria.pontual_monitor_api.domain.exception.PontualMonitorException;
import com.pontual_telemetria.pontual_monitor_api.domain.exception.person.PersonAlreadyExistsException;
import com.pontual_telemetria.pontual_monitor_api.domain.exception.user.UserAlreadyExistsException;
import com.pontual_telemetria.pontual_monitor_api.domain.model.user.AccountUser;
import com.pontual_telemetria.pontual_monitor_api.domain.model.person.Person;
import com.pontual_telemetria.pontual_monitor_api.domain.repository.PersonRepository;
import com.pontual_telemetria.pontual_monitor_api.domain.repository.UserRepository;
import com.pontual_telemetria.pontual_monitor_api.web.dto.user.AccountUserDTO;
import com.pontual_telemetria.pontual_monitor_api.web.dto.user.ResetPasswordDTO;
import com.pontual_telemetria.pontual_monitor_api.web.dto.user.UserRequestDTO;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDomainService {

    private final PasswordEncoder passwordEncoder;
    private final PersonRepository personRepository;
    private final UserRepository userRepository;

    public AccountUser createAccountUser(UserRequestDTO userRequest, Person person){
        return AccountUser.builder()
                .username(userRequest.getUsername())
                .password(passwordEncoder.encode(userRequest.getPassword()))
                .role(userRequest.getRole())
                .person(person)
                .enabled(userRequest.isEnabled())
                .build();

    }

    @Transactional
    public void resetPassword(ResetPasswordDTO resetPasswordDTO) {
        AccountUser accountUser = userRepository.findByUsername(resetPasswordDTO.getUsername());
        if (accountUser == null) {
            throw new PontualMonitorException("Usuário não encontrado", "USER_NOT_FOUND", HttpStatus.BAD_REQUEST, "O usuário informado não foi encontrado.");
        }

        if (passwordEncoder.matches(resetPasswordDTO.getNewPassword(), accountUser.getPassword())) {
            throw new PontualMonitorException("Nova senha igual à atual", "PASSWORD_SAME_AS_OLD", HttpStatus.BAD_REQUEST, "A nova senha deve ser diferente da atual.");
        }

        if (!isValidPassword(resetPasswordDTO.getNewPassword(), resetPasswordDTO.getConfirmNewPassword())) {
            throw new PontualMonitorException("Nova senha inválida", "INVALID_PASSWORD", HttpStatus.BAD_REQUEST, "A nova senha informada não atende aos requisitos de segurança.");
        }

        accountUser.setPassword(passwordEncoder.encode(resetPasswordDTO.getNewPassword()));
        userRepository.save(accountUser);
    }

    @Transactional
    public void update(AccountUserDTO accountUserDTO) {
        AccountUser user = userRepository.findById(accountUserDTO.getId())
                .orElseThrow(() -> new EntityNotFoundException("Conta de usuário não encontrada"));

        if(!user.getUsername().equals(accountUserDTO.getUsername())){
            verifyIfUsernameExists(accountUserDTO.getUsername());
        }

        user.setUsername(accountUserDTO.getUsername());
        user.setRole(accountUserDTO.getRole());
        user.setEnabled(accountUserDTO.getEnabled());
    }

    @Transactional
    public void delete(Long id) {
        AccountUser user = userRepository.findById(id)
                .orElseThrow(() -> new PontualMonitorException(
                        "Usuário não encontrado",
                        "USER_NOT_FOUND",
                        HttpStatus.BAD_REQUEST,
                        "Conta de usuário não encontrada")
                );
        userRepository.delete(user);
    }

    public void deleteByPersonId(Long personId){
        AccountUser accountUser = userRepository.findByPerson_Id(personId);
        if(accountUser != null) {
            userRepository.delete(accountUser);
        }
    }

    public void verifyIfPersonExists(String document){
        boolean isPersonExists = personRepository.existsByDocument(document);
        if(isPersonExists){
            throw new PersonAlreadyExistsException(document);
        }
    }

    public void verifyIfUsernameExists(String username) {
        boolean isUserExists = userRepository.existsByUsername(username);
        if(isUserExists){
            throw new UserAlreadyExistsException(username);
        }
    }

    public boolean isValidPassword(String newPassword, String confirmNewPassword) {
        String regex = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{6,}$";
        return newPassword.equals(confirmNewPassword) && newPassword.matches(regex);
    }
}
