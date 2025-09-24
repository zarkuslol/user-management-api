package br.com.sankhya.usermanager.domain.usecase;

import br.com.sankhya.usermanager.domain.model.Role;
import br.com.sankhya.usermanager.domain.model.User;
import br.com.sankhya.usermanager.domain.ports.inbound.UserManagementUseCase;
import br.com.sankhya.usermanager.domain.ports.inbound.dtos.ChangePasswordCommand;
import br.com.sankhya.usermanager.domain.ports.inbound.dtos.CreateUserCommand;
import br.com.sankhya.usermanager.domain.ports.inbound.dtos.UpdateUserEmailCommand;
import br.com.sankhya.usermanager.domain.ports.inbound.dtos.UpdateUserCommand;
import br.com.sankhya.usermanager.domain.ports.outbound.PasswordHasherPort;
import br.com.sankhya.usermanager.domain.ports.outbound.RoleRepositoryPort;
import br.com.sankhya.usermanager.domain.ports.outbound.UserRepositoryPort;
import br.com.sankhya.usermanager.domain.vo.Email;
import br.com.sankhya.usermanager.domain.vo.PasswordHash;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public class UserManagementUseCaseImpl implements UserManagementUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final RoleRepositoryPort roleRepositoryPort;
    private final PasswordHasherPort passwordHasherPort;

    // Injeção de dependências via construtor
    public UserManagementUseCaseImpl(UserRepositoryPort userRepositoryPort, RoleRepositoryPort roleRepositoryPort, PasswordHasherPort passwordHasherPort) {
        this.userRepositoryPort = userRepositoryPort;
        this.roleRepositoryPort = roleRepositoryPort;
        this.passwordHasherPort = passwordHasherPort;
    }

    @Override
    public User createUser(CreateUserCommand command) {
        // Valida se a Role existe
        Role userRole = roleRepositoryPort.findByName(command.role())
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + command.role()));

        // Criptografa a senha
        String hashedPassword = passwordHasherPort.hash(command.password());

        // Cria a entidade de domínio
        User newUser = new User();
        newUser.setUsername(command.username());
        newUser.setEmail(new Email(command.email()));
        newUser.setPassword(new PasswordHash(hashedPassword));
        newUser.setRole(userRole);
        newUser.setEnabled(true); // Por padrão, o usuário é criado ativo

        // Salva e retorna o usuário
        return userRepositoryPort.save(newUser);
    }

    // --- MÉTODOS AINDA NÃO IMPLEMENTADOS ---

    @Override
    public Page<User> listUsers(Pageable pageable) {
        // TODO: Implementar
        return null;
    }

    @Override
    public Page<User> listActiveUsers(Pageable pageable) {
        // TODO: Implementar
        return null;
    }

    @Override
    public Optional<User> findUserById(Long id) {
        // TODO: Implementar
        return Optional.empty();
    }

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
