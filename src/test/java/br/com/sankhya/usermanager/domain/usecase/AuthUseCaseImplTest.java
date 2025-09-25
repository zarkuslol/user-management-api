package br.com.sankhya.usermanager.domain.usecase;

import br.com.sankhya.usermanager.domain.model.User;
import br.com.sankhya.usermanager.domain.ports.inbound.AuthUseCase;
import br.com.sankhya.usermanager.domain.ports.inbound.dtos.LoginCommand;
import br.com.sankhya.usermanager.domain.ports.outbound.PasswordHasherPort;
import br.com.sankhya.usermanager.domain.ports.outbound.TokenServicePort;
import br.com.sankhya.usermanager.domain.ports.outbound.UserRepositoryPort;
import br.com.sankhya.usermanager.domain.vo.PasswordHash;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthUseCaseImplTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;
    @Mock
    private PasswordHasherPort passwordHasherPort;
    @Mock
    private TokenServicePort tokenServicePort;

    @InjectMocks
    private AuthUseCaseImpl authUseCase;

    @Test
    @DisplayName("Should return JWT token on successful login")
    void login_WithCorrectCredentials_ShouldReturnToken() {
        // Arrange
        LoginCommand command = new LoginCommand("testuser", "correctPassword");
        User user = new User();
        user.setUsername("testuser");
        user.setPassword(new PasswordHash("hashedPassword"));
        user.setEnabled(true);

        when(userRepositoryPort.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordHasherPort.check("correctPassword", "hashedPassword")).thenReturn(true);
        when(tokenServicePort.generateToken(user)).thenReturn("fake.jwt.token");

        // Act
        var response = authUseCase.login(command);

        // Assert
        assertNotNull(response);
        assertEquals("fake.jwt.token", response.token());
    }

    @Test
    @DisplayName("Should throw BadCredentialsException for wrong password")
    void login_WithWrongPassword_ShouldThrowException() {
        // Arrange
        LoginCommand command = new LoginCommand("testuser", "wrongPassword");
        User user = new User();
        user.setUsername("testuser");
        user.setPassword(new PasswordHash("hashedPassword"));

        when(userRepositoryPort.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordHasherPort.check("wrongPassword", "hashedPassword")).thenReturn(false);

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> authUseCase.login(command));
    }

    @Test
    @DisplayName("Should throw BadCredentialsException for non-existent user")
    void login_WithNonExistentUser_ShouldThrowException() {
        // Arrange
        LoginCommand command = new LoginCommand("nonexistent", "anyPassword");
        when(userRepositoryPort.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> authUseCase.login(command));
    }

    @Test
    @DisplayName("Should throw BadCredentialsException for a disabled user")
    void login_WithDisabledUser_ShouldThrowException() {
        // Arrange
        LoginCommand command = new LoginCommand("disableduser", "anyPassword");
        User disabledUser = new User();
        disabledUser.setUsername("disableduser");
        disabledUser.setPassword(new PasswordHash("hashedPassword"));
        disabledUser.setEnabled(false); // <-- O ponto chave do teste

        when(userRepositoryPort.findByUsername("disableduser")).thenReturn(Optional.of(disabledUser));

        // Act & Assert
        // A exceção deve ser a mesma para não dar pistas a um atacante
        assertThrows(BadCredentialsException.class, () -> authUseCase.login(command));
    }
}
