package br.com.sankhya.usermanager.domain.ports.inbound;

import br.com.sankhya.usermanager.domain.model.User;
import br.com.sankhya.usermanager.domain.ports.inbound.dtos.ChangePasswordCommand;
import br.com.sankhya.usermanager.domain.ports.inbound.dtos.CreateUserCommand;
import br.com.sankhya.usermanager.domain.ports.inbound.dtos.UpdateUserCommand;
import br.com.sankhya.usermanager.domain.ports.inbound.dtos.UpdateUserEmailCommand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

public interface UserManagementUseCase {
    // --- CREATE ---
    User createUser(CreateUserCommand command);

    // --- RETRIEVE ---
    Page<User> listUsers(Pageable pageable);
    Page<User> listActiveUsers(Pageable pageable);
    Optional<User> findUserById(Long id);

    // --- UPDATE ---
    Optional<User> updateUserProfile(Long id, UpdateUserCommand command);
    void updateUserEmail(Long id, UpdateUserEmailCommand command);
    void changeUserPassword(Long id, ChangePasswordCommand command);
    void activateUser(Long id);
    void deactivateUser(Long id);

    // --- DELETE ---
    void deleteUserById(Long id);
}
