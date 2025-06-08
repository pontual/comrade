package com.pontual_telemetria.pontual_monitor_api.web.controller;

import com.pontual_telemetria.pontual_monitor_api.application.service.UserApplicationService;
import com.pontual_telemetria.pontual_monitor_api.web.dto.user.UserRequestDTO;
import com.pontual_telemetria.pontual_monitor_api.web.dto.user.UserResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserApplicationService userApplicationService;

    @PostMapping("/create")
    ResponseEntity<UserResponseDTO> createUser(@RequestBody @Valid UserRequestDTO userRequest){
       UserResponseDTO response = userApplicationService.createUser(userRequest);
       return response != null ? ResponseEntity.ok(response) : ResponseEntity.noContent().build();
       }
}
