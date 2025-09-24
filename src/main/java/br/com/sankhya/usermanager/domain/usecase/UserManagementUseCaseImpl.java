package br.com.sankhya.usermanager.domain.usecase;

import br.com.sankhya.usermanager.domain.model.User;
import br.com.sankhya.usermanager.domain.ports.inbound.UserManagementUseCase;
import br.com.sankhya.usermanager.domain.ports.inbound.dtos.*;
import br.com.sankhya.usermanager.domain.ports.outbound.PasswordHasherPort;
import br.com.sankhya.usermanager.domain.ports.outbound.RoleRepositoryPort;
import br.com.sankhya.usermanager.domain.ports.outbound.UserRepositoryPort;
import br.com.sankhya.usermanager.domain.vo.Email;
import br.com.sankhya.usermanager.domain.vo.PasswordHash;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class UserManagementUseCaseImpl implements UserManagementUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final RoleRepositoryPort roleRepositoryPort;
    private final PasswordHasherPort passwordHasherPort;

    public UserManagementUseCaseImpl(UserRepositoryPort userRepositoryPort, RoleRepositoryPort roleRepositoryPort, PasswordHasherPort passwordHasherPort) {
        this.userRepositoryPort = userRepositoryPort;
        this.roleRepositoryPort = roleRepositoryPort;
        this.passwordHasherPort = passwordHasherPort;
    }

    @Override
    public User createUser(CreateUserCommand command) {
        // Lógica de criação que já tínhamos testado
        var role = roleRepositoryPort.findByName(command.role())
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + command.role()));

        var user = new User();
        user.setUsername(command.username());
        user.setEmail(new Email(command.email()));
        user.setPassword(new PasswordHash(passwordHasherPort.hash(command.password())));
        user.setRole(role);
        user.setEnabled(true);

        return userRepositoryPort.save(user);
    }

    @Override
    public Page<User> listUsers(Pageable pageable) {
        return userRepositoryPort.findAll(pageable);
    }

    @Override
    public Page<User> listActiveUsers(Pageable pageable) {
        return userRepositoryPort.findAllByEnabled(true, pageable);
    }

    @Override
    public Optional<User> findUserById(Long id) {
        return userRepositoryPort.findById(id);
    }

    // --- MÉTODOS AINDA NÃO IMPLEMENTADOS (STUBS) ---
    // Precisamos deles para satisfazer o contrato da interface

    @Override
    public Optional<User> updateUserProfile(Long id, UpdateUserCommand command) {
        // TODO: Implementar
        return Optional.empty();
    }

    @Override
    public void updateUserEmail(Long id, UpdateUserEmailCommand command) {
        // TODO: Implementar
    }

    @Override
    public void changeUserPassword(Long id, ChangePasswordCommand command) {
        // TODO: Implementar
    }

    @Override
    public void activateUser(Long id) {
        // TODO: Implementar
    }

    @Override
    public void deactivateUser(Long id) {
        // TODO: Implementar
    }

    @Override
    public void deleteUserById(Long id) {
        // TODO: Implementar
    }
}
