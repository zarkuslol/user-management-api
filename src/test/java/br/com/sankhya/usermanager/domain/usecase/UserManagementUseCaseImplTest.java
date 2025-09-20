package br.com.sankhya.usermanager.domain.usecase;

import br.com.sankhya.usermanager.domain.model.Role;
import br.com.sankhya.usermanager.domain.model.User;
import br.com.sankhya.usermanager.domain.ports.inbound.dtos.CreateUserCommand;
import br.com.sankhya.usermanager.domain.ports.outbound.PasswordHasherPort;
import br.com.sankhya.usermanager.domain.ports.outbound.RoleRepositoryPort;
import br.com.sankhya.usermanager.domain.ports.outbound.UserRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Habilita o Mockito para este teste
class UserManagementUseCaseImplTest {

    // Cria um mock para cada port de saída (dependência)
    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Mock
    private RoleRepositoryPort roleRepositoryPort;

    @Mock
    private PasswordHasherPort passwordHasherPort;

    // Injeta os mocks criados acima na nossa classe sob teste
    @InjectMocks
    private UserManagementUseCaseImpl userManagementUseCase;

    @Test
    @DisplayName("Should create user successfully when given valid data")
    void createUser_WithValidData_ShouldSucceed() {
        // Arrange (Organizar)
        CreateUserCommand command = new CreateUserCommand(
                "john.doe",
                "john.doe@example.com",
                "password123",
                "ADMIN"
        );

        Role adminRole = new Role(); // Simula um objeto Role
        adminRole.setId(1L);
        adminRole.setName("ADMIN");

        // Configura o comportamento dos mocks
        when(roleRepositoryPort.findByName("ADMIN")).thenReturn(Optional.of(adminRole));
        when(passwordHasherPort.hash("password123")).thenReturn("hashedPassword");
        // Quando o save for chamado, retorna o próprio objeto User que foi passado
        when(userRepositoryPort.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act (Agir)
        User createdUser = userManagementUseCase.createUser(command);

        // Assert (Verificar)
        assertNotNull(createdUser);
        assertEquals("john.doe", createdUser.getUsername());
        assertEquals("john.doe@example.com", createdUser.getEmail().address());
        assertEquals("hashedPassword", createdUser.getPassword().hash());
        assertTrue(createdUser.isEnabled());

        // --- CORREÇÕES AQUI ---
        assertNotNull(createdUser.getRole(), "The user's role should not be null");
        assertEquals("ADMIN", createdUser.getRole().getName(), "The user's role should be ADMIN");

        // Verifica se os mocks foram chamados como esperado
        verify(roleRepositoryPort, times(1)).findByName("ADMIN");
        verify(passwordHasherPort, times(1)).hash("password123");
        verify(userRepositoryPort, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when role is not found")
    void createUser_WithInvalidRole_ShouldThrowException() {
        // Arrange
        CreateUserCommand command = new CreateUserCommand(
                "jane.doe",
                "jane.doe@example.com",
                "password123",
                "INVALID_ROLE"
        );

        // Configura o mock para simular que a role não foi encontrada
        when(roleRepositoryPort.findByName("INVALID_ROLE")).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> userManagementUseCase.createUser(command) // Executa o método que deve lançar a exceção
        );

        assertEquals("Role not found: INVALID_ROLE", thrown.getMessage());

        // Garante que a lógica parou antes de tentar salvar o usuário ou hashear a senha
        verify(passwordHasherPort, never()).hash(anyString());
        verify(userRepositoryPort, never()).save(any(User.class));
    }
}
