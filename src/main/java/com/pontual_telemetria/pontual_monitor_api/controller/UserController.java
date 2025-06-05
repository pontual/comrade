package com.pontual_telemetria.pontual_monitor_api.controller;

import com.pontual_telemetria.pontual_monitor_api.dto.user.UserRequestDTO;
import com.pontual_telemetria.pontual_monitor_api.dto.user.UserResponseDTO;
import com.pontual_telemetria.pontual_monitor_api.service.UserService;
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

    private final UserService userService;

    @PostMapping("/create")
    ResponseEntity<UserResponseDTO> create(@RequestBody @Valid UserRequestDTO userRequest){
       UserResponseDTO response = userService.create(userRequest);
       if(response != null){
           return ResponseEntity.ok(response);
       } else {
           return ResponseEntity.noContent().build();
       }
    }
}
