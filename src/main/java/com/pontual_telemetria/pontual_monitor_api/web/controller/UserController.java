package com.pontual_telemetria.pontual_monitor_api.web.controller;

import com.pontual_telemetria.pontual_monitor_api.application.service.UserApplicationService;
import com.pontual_telemetria.pontual_monitor_api.web.dto.user.*;
import com.pontual_telemetria.pontual_monitor_api.web.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserApplicationService userApplicationService;

    @Operation(
            summary = "Listar usuários cadastrados",
            description = "Retorna todos os cadastros existentes em paginação"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuários retornada com sucesso", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = PageAccountUserDTO.class)
            )),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
            )),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
            ))
    })
    @GetMapping("/account-users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<AccountUserDetailsDTO>> getAllPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(userApplicationService.getAllPaginated(page, size));
    }

    @Operation(
            summary = "Busca usuário por CPF",
            description = "Retorna dados de cadastro por número do CPF"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dados de usuário retornado com sucesso", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = AccountUserDetailsDTO.class)
            )),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
            )),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
            ))
    })
    @GetMapping("/account-user/{cpf}")
    ResponseEntity<AccountUserDetailsDTO> getByCPF(@PathVariable String cpf) {
        return ResponseEntity.ok(userApplicationService.getByCPF(cpf));
    }

    @Operation(
            summary = "Cadastramento de usuários",
            description = "Recebe informações para cadastramento de nova pessoa/usuário"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cadastro realizado com sucesso", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserResponseDTO.class)
            )),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
            )),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
            ))
    })
    @PostMapping("/create")
        //TODO remover tags de comentarios
//    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<UserResponseDTO> create(@RequestBody @Valid UserRequestDTO userRequest){
       UserResponseDTO response = userApplicationService.create(userRequest);
       return response != null ? ResponseEntity.ok(response) : ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Atualiza senha de acesso do usuário",
            description = "Realiza a atualização da senha de acesso do usuário"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Senha de acesso atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
            )),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
            ))
    })
    @PutMapping("/reset-password")
    ResponseEntity<Void> resetPassword(@RequestBody @Valid ResetPasswordDTO resetPasswordDTO){
        userApplicationService.resetPassword(resetPasswordDTO);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Atualização de conta de usuário",
            description = "Atualiza dados usuário/role de usuário"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuário atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
            )),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
            ))
    })
    @PutMapping("/update")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<Void> update(@RequestBody @Valid AccountUserDTO accountUserDTO){
        userApplicationService.update(accountUserDTO);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Deleta dados de acesso",
            description = "Deleta dados de acesso do usuário"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuário excluído com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
            )),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
            ))
    })
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<Void> delete(@PathVariable Long id){
        userApplicationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
