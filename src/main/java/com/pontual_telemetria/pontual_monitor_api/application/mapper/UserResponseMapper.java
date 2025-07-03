package com.pontual_telemetria.pontual_monitor_api.application.mapper;

import com.pontual_telemetria.pontual_monitor_api.domain.model.person.Person;
import com.pontual_telemetria.pontual_monitor_api.web.dto.user.UserResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserResponseMapper {

    @Mapping(target = "id", source = "person.id")
    @Mapping(target = "username", source = "username")
    @Mapping(target = "role", source = "role")
    @Mapping(target = "enabled", source = "enabled")
    @Mapping(target = "personName", source = "person.name")
    @Mapping(target = "document", source = "person.document")
    @Mapping(target = "email", source = "person.email")
    @Mapping(target = "phone", source = "person.phone")
    UserResponseDTO toDTO(Person person, String username, String role, boolean enabled);
}
