package br.com.sankhya.usermanager.domain.ports.inbound;

import br.com.sankhya.usermanager.domain.model.User;
import br.com.sankhya.usermanager.domain.ports.inbound.dtos.CreateUserCommand;
import br.com.sankhya.usermanager.domain.ports.inbound.dtos.UpdateUserCommand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserManagementUseCase {
    Page<User> listAllUsers(Pageable pageable);
    Page<User> listAllActiveUsers(Pageable pageable);
    Optional<User> findUserById(Long id);
    User createUser(CreateUserCommand command);
    Optional<User> updateUser(Long id, UpdateUserCommand command);
    void deleteUserById(Long id);
    void activateUser(Long id);
    void deactivateUser(Long id);
}
