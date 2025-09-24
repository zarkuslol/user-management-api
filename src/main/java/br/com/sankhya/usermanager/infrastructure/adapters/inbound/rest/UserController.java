package br.com.sankhya.usermanager.infrastructure.adapters.inbound.rest;

import br.com.sankhya.usermanager.domain.model.User;
import br.com.sankhya.usermanager.domain.ports.inbound.UserManagementUseCase;
import br.com.sankhya.usermanager.domain.ports.inbound.dtos.*;
import br.com.sankhya.usermanager.infrastructure.adapters.inbound.rest.dto.UserResponseDTO;
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
public class UserController {

    private final UserManagementUseCase userManagementUseCase;

    public UserController(UserManagementUseCase userManagementUseCase) {
        this.userManagementUseCase = userManagementUseCase;
    }

    // --- CREATE ---
    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody @Valid CreateUserCommand command) {
        User createdUser = userManagementUseCase.createUser(command);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(createdUser.getId()).toUri();
        return ResponseEntity.created(location).body(UserResponseDTO.fromDomain(createdUser));
    }

    // --- RETRIEVE ---
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> findUserById(@PathVariable Long id) {
        return userManagementUseCase.findUserById(id)
                .map(user -> ResponseEntity.ok(UserResponseDTO.fromDomain(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<Page<UserResponseDTO>> listAllUsers(Pageable pageable) {
        Page<User> users = userManagementUseCase.listUsers(pageable);
        Page<UserResponseDTO> responsePage = users.map(UserResponseDTO::fromDomain);
        return ResponseEntity.ok(responsePage);
    }

    @GetMapping("/active")
    public ResponseEntity<Page<UserResponseDTO>> listActiveUsers(Pageable pageable) {
        Page<User> users = userManagementUseCase.listActiveUsers(pageable);
        Page<UserResponseDTO> responsePage = users.map(UserResponseDTO::fromDomain);
        return ResponseEntity.ok(responsePage);
    }

    // --- UPDATE ---
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUserProfile(@PathVariable Long id, @RequestBody @Valid UpdateUserCommand command) {
        return userManagementUseCase.updateUserProfile(id, command)
                .map(user -> ResponseEntity.ok(UserResponseDTO.fromDomain(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/email")
    public ResponseEntity<Void> updateUserEmail(@PathVariable Long id, @RequestBody @Valid UpdateUserEmailCommand command) {
        userManagementUseCase.updateUserEmail(id, command);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/password")
    public ResponseEntity<Void> changeUserPassword(@PathVariable Long id, @RequestBody @Valid ChangePasswordCommand command) {
        userManagementUseCase.changeUserPassword(id, command);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activateUser(@PathVariable Long id) {
        userManagementUseCase.activateUser(id);
    }

    @PatchMapping("/{id}/deactivate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivateUser(@PathVariable Long id) {
        userManagementUseCase.deactivateUser(id);
    }

    // --- DELETE ---
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        userManagementUseCase.deleteUserById(id);
    }
}