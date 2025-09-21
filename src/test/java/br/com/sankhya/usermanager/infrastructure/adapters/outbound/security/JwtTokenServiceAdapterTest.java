package br.com.sankhya.usermanager.infrastructure.adapters.outbound.security;

import br.com.sankhya.usermanager.domain.model.User;
import br.com.sankhya.usermanager.domain.ports.outbound.ClockProviderPort;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtTokenServiceAdapterTest {

    @Mock
    private ClockProviderPort clockProvider; // Agora usamos um mock para o tempo

    private JwtTokenServiceAdapter tokenService;

    private final String testSecret = "MySuperSecretKeyForTestingPurposesThatIsLongEnough";
    private final long oneHourInMillis = 3600000;

    @BeforeEach
    void setUp() {
        // Injetamos o mock do ClockProvider diretamente no construtor
        tokenService = new JwtTokenServiceAdapter(testSecret, oneHourInMillis, clockProvider);
    }

    @Test
    @DisplayName("Should generate a valid JWT token for a given user")
    void generateToken_ShouldReturnValidToken() {
        // Arrange
        User user = new User();
        user.setUsername("testuser");
        LocalDateTime fixedTime = LocalDateTime.of(2025, Month.SEPTEMBER, 21, 10, 0, 0);
        when(clockProvider.now()).thenReturn(fixedTime); // Controlamos o "agora"

        // Act
        String token = tokenService.generateToken(user);

        // Assert
        assertNotNull(token);
        assertEquals("testuser", tokenService.getUsernameFromToken(token));
    }

    @Test
    @DisplayName("Should throw SignatureException for a token signed with a different key")
    void getUsernameFromToken_WithInvalidSignature_ShouldThrowException() {
        // Arrange
        User user = new User();
        user.setUsername("testuser");
        LocalDateTime fixedTime = LocalDateTime.of(2025, Month.SEPTEMBER, 21, 10, 0, 0);
        when(clockProvider.now()).thenReturn(fixedTime);
        String token = tokenService.generateToken(user);

        // Criamos um segundo service com uma chave secreta DIFERENTE
        String wrongSecret = "ThisIsTheWrongSecretKeyAndItMustAlsoBeLong";
        JwtTokenServiceAdapter invalidTokenService = new JwtTokenServiceAdapter(wrongSecret, oneHourInMillis, clockProvider);

        // Act & Assert
        assertThrows(SignatureException.class, () -> invalidTokenService.getUsernameFromToken(token));
    }

    @Test
    @DisplayName("Should throw ExpiredJwtException for an expired token")
    void getUsernameFromToken_WithExpiredToken_ShouldThrowException() {
        // Arrange
        User user = new User();
        user.setUsername("testuser");
        LocalDateTime tokenCreationTime = LocalDateTime.of(2025, Month.SEPTEMBER, 21, 10, 0, 0);

        // Fase 1: Criação do Token
        when(clockProvider.now()).thenReturn(tokenCreationTime);
        String token = tokenService.generateToken(user);

        // Fase 2: Validação do Token (Avançamos o tempo)
        LocalDateTime timeAfterExpiration = tokenCreationTime.plusHours(1).plusSeconds(1);

        // Usamos lenient() para dizer ao Mockito que essa redefinição de comportamento é intencional
        lenient().when(clockProvider.now()).thenReturn(timeAfterExpiration);

        // Act & Assert
        assertThrows(ExpiredJwtException.class, () -> tokenService.getUsernameFromToken(token));
    }
}
