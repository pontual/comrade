package com.pontual_telemetria.pontual_monitor_api.application.mapper;

import com.pontual_telemetria.pontual_monitor_api.domain.model.person.Person;
import com.pontual_telemetria.pontual_monitor_api.web.dto.user.UserRequestDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PersonMapper {
    Person toEntity(UserRequestDTO dto);
}
