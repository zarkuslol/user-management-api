package br.com.sankhya.usermanager.infrastructure.adapters.inbound.rest;

import br.com.sankhya.usermanager.domain.model.User;
import br.com.sankhya.usermanager.domain.ports.inbound.UserManagementUseCase;
import br.com.sankhya.usermanager.domain.ports.inbound.dtos.*;
import br.com.sankhya.usermanager.infrastructure.adapters.inbound.rest.dto.ErrorResponseDTO;
import br.com.sankhya.usermanager.infrastructure.adapters.inbound.rest.dto.UserResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/users")
@Tag(name = "Gerenciamento de Usuários", description = "Endpoints para criar, ler, atualizar e deletar usuários.")
public class UserController {

    private final UserManagementUseCase userManagementUseCase;

    public UserController(UserManagementUseCase userManagementUseCase) {
        this.userManagementUseCase = userManagementUseCase;
    }

    // --- CREATE ---
    @PostMapping
    @Operation(summary = "Cria um novo usuário", description = "Registra um novo usuário no sistema. O username e e-mail devem ser únicos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "Usuário com este username ou e-mail já existe",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody @Valid CreateUserCommand command) {
        User createdUser = userManagementUseCase.createUser(command);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(createdUser.getId()).toUri();
        return ResponseEntity.created(location).body(UserResponseDTO.fromDomain(createdUser));
    }

    // --- RETRIEVE ---
    @GetMapping("/{id}")
    @Operation(summary = "Busca um usuário por ID", description = "Retorna os detalhes de um usuário específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<UserResponseDTO> findUserById(@PathVariable Long id) {
        return userManagementUseCase.findUserById(id)
                .map(user -> ResponseEntity.ok(UserResponseDTO.fromDomain(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "Lista todos os usuários com paginação", description = "Retorna uma lista paginada de todos os usuários, ativos e inativos.")
    public ResponseEntity<Page<UserResponseDTO>> listAllUsers(Pageable pageable) {
        Page<User> users = userManagementUseCase.listUsers(pageable);
        Page<UserResponseDTO> responsePage = users.map(UserResponseDTO::fromDomain);
        return ResponseEntity.ok(responsePage);
    }

    @GetMapping("/active")
    @Operation(summary = "Lista usuários ativos com paginação", description = "Retorna uma lista paginada apenas dos usuários que estão ativos no sistema.")
    public ResponseEntity<Page<UserResponseDTO>> listActiveUsers(Pageable pageable) {
        Page<User> users = userManagementUseCase.listActiveUsers(pageable);
        Page<UserResponseDTO> responsePage = users.map(UserResponseDTO::fromDomain);
        return ResponseEntity.ok(responsePage);
    }

    // --- UPDATE ---
    @PutMapping("/{id}")
    @Operation(summary = "Atualiza o perfil de um usuário", description = "Atualiza o username e o perfil (role) de um usuário existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<UserResponseDTO> updateUserProfile(
            @Parameter(description = "ID do usuário a ser atualizado", required = true) @PathVariable Long id,
             @RequestBody @Valid UpdateUserCommand command) {
        return userManagementUseCase.updateUserProfile(id, command)
                .map(user -> ResponseEntity.ok(UserResponseDTO.fromDomain(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/email")
    @Operation(summary = "Atualiza o e-mail de um usuário", description = "Atualiza o e-mail de um usuário. O novo e-mail não pode estar em uso por outra conta.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "E-mail atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "409", description = "O e-mail fornecido já está em uso")
    })
    public ResponseEntity<Void> updateUserEmail(
            @Parameter(description = "ID do usuário a ser atualizado", required = true) @PathVariable Long id,
            @RequestBody @Valid UpdateUserEmailCommand command) {
        userManagementUseCase.updateUserEmail(id, command);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/password")
    @Operation(summary = "Altera a senha do usuário", description = "Permite que um usuário autenticado altere sua própria senha fornecendo a antiga e a nova.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Senha alterada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Senha antiga incorreta"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<Void> changeUserPassword(
            @Parameter(description = "ID do usuário", required = true) @PathVariable Long id,
            @RequestBody @Valid ChangePasswordCommand command) {
        userManagementUseCase.changeUserPassword(id, command);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Ativa um usuário", description = "Muda o status de um usuário para 'ativo'.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuário ativado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public void activateUser(
            @Parameter(description = "ID do usuário a ser ativado", required = true) @PathVariable Long id) {
        userManagementUseCase.activateUser(id);
    }

    @PatchMapping("/{id}/deactivate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Desativa um usuário", description = "Muda o status de um usuário para 'inativo', impedindo seu login.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuário desativado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public void deactivateUser(
            @Parameter(description = "ID do usuário a ser desativado", required = true) @PathVariable Long id) {
        userManagementUseCase.deactivateUser(id);
    }

    // --- DELETE ---
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Deleta um usuário", description = "Remove permanentemente um usuário do sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuário deletado com sucesso (mesmo que não existisse)")
    })
    public void deleteUser(
            @Parameter(description = "ID do usuário a ser deletado", required = true) @PathVariable Long id) {
        userManagementUseCase.deleteUserById(id);
    }
}
