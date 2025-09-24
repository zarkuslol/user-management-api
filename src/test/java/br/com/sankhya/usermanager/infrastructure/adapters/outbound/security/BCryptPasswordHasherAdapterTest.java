package br.com.sankhya.usermanager.infrastructure.adapters.outbound.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

class BCryptPasswordHasherAdapterTest {

    private BCryptPasswordHasherAdapter passwordHasherAdapter;

    @BeforeEach
    void setUp() {
        // Arrange (Organizar)
        // Criamos uma instância real do encoder, pois ele não tem dependências externas
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        // Instanciamos nosso adaptador com a implementação real
        passwordHasherAdapter = new BCryptPasswordHasherAdapter(passwordEncoder);
    }

    @Test
    @DisplayName("Should hash password successfully")
    void hash_ShouldReturnHashedPassword() {
        // Arrange
        String rawPassword = "mySecurePassword123";

        // Act (Agir)
        String hashedPassword = passwordHasherAdapter.hash(rawPassword);

        // Assert (Verificar)
        assertNotNull(hashedPassword);
        assertNotEquals(rawPassword, hashedPassword);
        // O BCrypt gera um hash diferente a cada vez, então a única forma de verificar
        // é usando o método 'matches', que é o que testamos a seguir.
    }

    @Test
    @DisplayName("Should return true when raw password matches hashed password")
    void check_WhenPasswordsMatch_ShouldReturnTrue() {
        // Arrange
        String rawPassword = "mySecurePassword123";
        String hashedPassword = passwordHasherAdapter.hash(rawPassword);

        // Act
        boolean matches = passwordHasherAdapter.check(rawPassword, hashedPassword);

        // Assert
        assertTrue(matches);
    }

    @Test
    @DisplayName("Should return false when raw password does not match hashed password")
    void check_WhenPasswordsDoNotMatch_ShouldReturnFalse() {
        // Arrange
        String rawPassword = "mySecurePassword123";
        String wrongPassword = "wrongPassword";
        String hashedPassword = passwordHasherAdapter.hash(rawPassword);

        // Act
        boolean matches = passwordHasherAdapter.check(wrongPassword, hashedPassword);

        // Assert
        assertFalse(matches);
    }
}
