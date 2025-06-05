package com.pontual_telemetria.pontual_monitor_api.service;

import com.pontual_telemetria.pontual_monitor_api.domain.account_user.AccountUser;
import com.pontual_telemetria.pontual_monitor_api.domain.person.Person;
import com.pontual_telemetria.pontual_monitor_api.dto.user.UserRequestDTO;
import com.pontual_telemetria.pontual_monitor_api.dto.user.UserResponseDTO;
import com.pontual_telemetria.pontual_monitor_api.repository.PersonRepository;
import com.pontual_telemetria.pontual_monitor_api.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PersonRepository personRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponseDTO create(UserRequestDTO userRequest) {
        boolean isCreateAccountUser = userRequest.getIsCreateAccountUser();

        Person person = Person.builder()
                .name(userRequest.getName())
                .document(userRequest.getDocument())
                .email(userRequest.getEmail())
                .phone(userRequest.getPhone())
                .build();

        personRepository.save(person);

        if(isCreateAccountUser) {
            AccountUser user = AccountUser.builder()
                    .username(userRequest.getUsername())
                    .password(passwordEncoder.encode(userRequest.getPassword()))
                    .role(userRequest.getRole())
                    .person(person)
                    .build();

            userRepository.save(user);
        }
        return UserResponseDTO.builder()
                .id(person.getId())
                .username(userRequest.getUsername())
                .role(userRequest.getRole())
                .personName(person.getName())
                .document(person.getDocument())
                .email(person.getEmail())
                .phone(person.getPhone())
                .build();
    }
}
