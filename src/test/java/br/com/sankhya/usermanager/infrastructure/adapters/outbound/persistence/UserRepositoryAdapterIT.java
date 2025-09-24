package br.com.sankhya.usermanager.infrastructure.adapters.outbound.persistence;

import br.com.sankhya.usermanager.TestcontainersConfiguration;
import br.com.sankhya.usermanager.domain.model.User;
import br.com.sankhya.usermanager.domain.vo.Email;
import br.com.sankhya.usermanager.infrastructure.adapters.outbound.persistence.entities.RoleEntity;
import br.com.sankhya.usermanager.infrastructure.adapters.outbound.persistence.entities.UserEntity;
import br.com.sankhya.usermanager.infrastructure.adapters.outbound.persistence.mappers.UserMapper;
import br.com.sankhya.usermanager.infrastructure.adapters.outbound.persistence.repositories.JpaRoleRepository;
import br.com.sankhya.usermanager.infrastructure.adapters.outbound.persistence.repositories.JpaUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest // Foco total na camada de persistência
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // Usa nosso banco do Testcontainers
@Import({TestcontainersConfiguration.class, UserRepositoryAdapter.class, UserMapper.class}) // Importa o necessário
@ActiveProfiles("test")
class UserRepositoryAdapterIT {

    @Autowired
    private JpaUserRepository jpaRepository; // Para setup e verificação direta no banco
    @Autowired
    private JpaRoleRepository roleRepository;
    @Autowired
    private UserRepositoryAdapter userRepositoryAdapter; // A classe que estamos testando

    @BeforeEach
    void setUp() {
        // Garante que o banco esteja limpo antes de cada teste
        jpaRepository.deleteAll();

        // Salva uma role para usar nos testes
        RoleEntity adminRole = roleRepository.findByName("ADMIN").get();

        // Cria e salva usuários de teste
        UserEntity activeUser = new UserEntity();
        activeUser.setUsername("activeuser");
        activeUser.setPassword("password");
        activeUser.setEmail("active@test.com");
        activeUser.setEnabled(true);
        activeUser.setRole(adminRole);
        jpaRepository.save(activeUser);

        UserEntity inactiveUser = new UserEntity();
        inactiveUser.setUsername("inactiveuser");
        inactiveUser.setPassword("password");
        inactiveUser.setEmail("inactive@test.com");
        inactiveUser.setEnabled(false);
        inactiveUser.setRole(adminRole);
        jpaRepository.save(inactiveUser);
    }

    @Test
    @DisplayName("Should find user by username when user exists")
    void findByUsername_WhenUserExists_ShouldReturnUser() {
        // Act
        Optional<User> foundUser = userRepositoryAdapter.findByUsername("activeuser");

        // Assert
        assertTrue(foundUser.isPresent());
        assertEquals("activeuser", foundUser.get().getUsername());
    }

    @Test
    @DisplayName("Should return empty optional when user does not exist")
    void findByUsername_WhenUserDoesNotExist_ShouldReturnEmpty() {
        // Act
        Optional<User> foundUser = userRepositoryAdapter.findByUsername("nonexistent");

        // Assert
        assertTrue(foundUser.isEmpty());
    }

    @Test
    @DisplayName("Should find user by email when user exists")
    void findByEmail_WhenUserExists_ShouldReturnUser() {
        // Act
        Optional<User> foundUser = userRepositoryAdapter.findByEmail(new Email("active@test.com"));

        // Assert
        assertTrue(foundUser.isPresent());
        assertEquals("active@test.com", foundUser.get().getEmail().address());
    }

    @Test
    @DisplayName("Should find only enabled users when findAllByEnabled is true")
    void findAllByEnabled_WhenTrue_ShouldReturnOnlyEnabledUsers() {
        // Arrange
        PageRequest pageable = PageRequest.of(0, 10);

        // Act
        Page<User> resultPage = userRepositoryAdapter.findAllByEnabled(true, pageable);

        // Assert
        assertEquals(1, resultPage.getTotalElements());
        assertEquals("activeuser", resultPage.getContent().get(0).getUsername());
    }

    @Test
    @DisplayName("Should find only disabled users when findAllByEnabled is false")
    void findAllByEnabled_WhenFalse_ShouldReturnOnlyDisabledUsers() {
        // Arrange
        PageRequest pageable = PageRequest.of(0, 10);

        // Act
        Page<User> resultPage = userRepositoryAdapter.findAllByEnabled(false, pageable);

        // Assert
        assertEquals(1, resultPage.getTotalElements());
        assertEquals("inactiveuser", resultPage.getContent().get(0).getUsername());
    }
}
