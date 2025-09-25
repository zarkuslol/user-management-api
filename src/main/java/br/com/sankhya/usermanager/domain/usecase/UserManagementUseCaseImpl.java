package br.com.sankhya.usermanager.domain.usecase;

import br.com.sankhya.usermanager.domain.model.Role;
import br.com.sankhya.usermanager.domain.model.User;
import br.com.sankhya.usermanager.domain.model.exceptions.UserAlreadyExistsException;
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
        // 1. Valida se o username já existe
        userRepositoryPort.findByUsername(command.username()).ifPresent(user -> {
            throw new UserAlreadyExistsException("Username already exists: " + command.username());
        });

        // 2. Valida se o e-mail já existe
        userRepositoryPort.findByEmail(new Email(command.email())).ifPresent(user -> {
            throw new UserAlreadyExistsException("Email already registered: " + command.email());
        });

        // 1. Busca a Role no banco de dados através da porta de persistência
        Role userRole = roleRepositoryPort.findByName(command.role())
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + command.role()));

        // 2. Criptografa a senha recebida usando a porta de hashing
        String hashedPassword = passwordHasherPort.hash(command.password());

        // 3. Cria o novo objeto de domínio User
        User newUser = new User();
        newUser.setUsername(command.username());
        newUser.setEmail(new Email(command.email()));
        newUser.setPassword(new PasswordHash(hashedPassword));
        newUser.setRole(userRole);
        newUser.setEnabled(true); // Usuários são criados como ativos por padrão

        // 4. Salva o novo usuário através da porta de persistência e o retorna
        return userRepositoryPort.save(newUser);
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
        // 1. Busca o usuário existente pelo ID
        return userRepositoryPort.findById(id)
                .map(userToUpdate -> {
                    // 2. Busca a nova Role pelo nome
                    Role newRole = roleRepositoryPort.findByName(command.role())
                            .orElseThrow(() -> new IllegalArgumentException("Role not found: " + command.role()));

                    // 3. Atualiza os campos do objeto de domínio
                    userToUpdate.setUsername(command.username());
                    userToUpdate.setRole(newRole);

                    // 4. Salva o usuário atualizado e retorna
                    return userRepositoryPort.save(userToUpdate);
                });
    }

    @Override
    public void updateUserEmail(Long id, UpdateUserEmailCommand command) {
        // 1. Busca o usuário que será atualizado
        User userToUpdate = userRepositoryPort.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found")); // Idealmente, uma exceção customizada

        // 2. Verifica se o novo e-mail já está em uso por OUTRO usuário
        Email newEmail = new Email(command.newEmail());
        userRepositoryPort.findByEmail(newEmail).ifPresent(existingUser -> {
            if (!existingUser.getId().equals(id)) {
                throw new UserAlreadyExistsException("Email already registered: " + newEmail.address());
            }
        });

        // 3. Atualiza o e-mail e salva
        userToUpdate.setEmail(newEmail);
        userRepositoryPort.save(userToUpdate);
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
        // 1. Verifica se o usuário existe antes de tentar deletar
        userRepositoryPort.findById(id).ifPresent(user -> {
            // 2. Se existe, chama a porta de persistência para deletar
            userRepositoryPort.deleteById(id);
        });
        // NOTA: Se o usuário não existe, a operação termina silenciosamente.
        // Isso é uma decisão de design para não expor quais IDs são válidos ou não.
    }
}
