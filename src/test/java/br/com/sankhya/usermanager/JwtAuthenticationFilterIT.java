package br.com.sankhya.usermanager;

import br.com.sankhya.usermanager.domain.model.Role;
import br.com.sankhya.usermanager.domain.model.User;
import br.com.sankhya.usermanager.domain.ports.outbound.TokenServicePort;
import br.com.sankhya.usermanager.infrastructure.adapters.outbound.persistence.entities.RoleEntity;
import br.com.sankhya.usermanager.infrastructure.adapters.outbound.persistence.entities.UserEntity;
import br.com.sankhya.usermanager.infrastructure.adapters.outbound.persistence.repositories.JpaRoleRepository;
import br.com.sankhya.usermanager.infrastructure.adapters.outbound.persistence.repositories.JpaUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
@Transactional
class JwtAuthenticationFilterIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenServicePort tokenService;

    @Autowired
    private JpaUserRepository userRepository;

    @Autowired
    private JpaRoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        RoleEntity viewerRole = roleRepository.findByName("VIEWER")
                .orElseThrow(() -> new IllegalStateException("Default role VIEWER not found."));

        UserEntity testUser = new UserEntity();
        testUser.setUsername("testuser");
        testUser.setPassword(passwordEncoder.encode("password"));
        testUser.setEmail("test@user.com");
        testUser.setEnabled(true);
        testUser.setRole(viewerRole);
        userRepository.save(testUser);
    }

    @Test
    @DisplayName("Should deny access to protected endpoint without token")
    void givenNoToken_whenAccessProtectedEndpoint_thenForbidden() throws Exception {
        // ATUALIZADO: Usando um endpoint real e protegido
        mockMvc.perform(get("/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should allow access to protected endpoint with valid token")
    void givenValidToken_whenAccessProtectedEndpoint_thenOk() throws Exception {
        // Arrange: Gerar um token válido para o usuário que criamos no setUp()
        User mockUser = new User();
        mockUser.setUsername("testuser");
        mockUser.setRole(new Role(2L, "VIEWER"));
        String validToken = tokenService.generateToken(mockUser);

        // Act & Assert: ATUALIZADO para usar o endpoint /users
        mockMvc.perform(get("/users")
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk());
    }
}
