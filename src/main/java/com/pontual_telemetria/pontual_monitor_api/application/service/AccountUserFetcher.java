package com.pontual_telemetria.pontual_monitor_api.application.service;

import com.pontual_telemetria.pontual_monitor_api.application.mapper.AccountUserMapper;
import com.pontual_telemetria.pontual_monitor_api.domain.model.user.AccountUser;
import com.pontual_telemetria.pontual_monitor_api.domain.repository.UserRepository;
import com.pontual_telemetria.pontual_monitor_api.web.dto.user.AccountUserDetailsDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountUserFetcher {

    private final UserRepository userRepository;
    private final AccountUserMapper accountUserMapper;

    public Page<AccountUserDetailsDTO> getAllPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AccountUser> accountUsers = userRepository.findAll(pageable);
        return accountUsers.map(accountUserMapper::toDto);
    }

    public AccountUserDetailsDTO getByCPF(String cpf) {
        AccountUser accountUser = userRepository.findByPerson_Document(cpf);
        return accountUserMapper.toDto(accountUser);
    }
}
