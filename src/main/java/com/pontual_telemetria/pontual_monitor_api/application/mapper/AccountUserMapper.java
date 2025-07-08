package com.pontual_telemetria.pontual_monitor_api.application.mapper;

import com.pontual_telemetria.pontual_monitor_api.domain.model.user.AccountUser;
import com.pontual_telemetria.pontual_monitor_api.web.dto.user.AccountUserDetailsDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountUserMapper {
    AccountUserDetailsDTO toDto(AccountUser entity);
}
