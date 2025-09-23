package br.com.sankhya.usermanager;

import br.com.sankhya.usermanager.domain.model.User;
import br.com.sankhya.usermanager.domain.ports.outbound.TokenServicePort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
class JwtAuthenticationFilterIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenServicePort tokenService; // Usamos o token service real para gerar um token válido

    @Test
    @DisplayName("Should deny access to protected endpoint without token")
    void givenNoToken_whenAccessProtectedEndpoint_thenForbidden() throws Exception {
        mockMvc.perform(get("/api/test/hello"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should allow access to protected endpoint with valid token")
    @WithMockUser("testuser") // Simula um usuário no banco para o TokenService usar
    void givenValidToken_whenAccessProtectedEndpoint_thenOk() throws Exception {
        // Arrange: Gerar um token válido
        User mockUser = new User();
        mockUser.setUsername("testuser");
        String validToken = tokenService.generateToken(mockUser);

        // Act & Assert
        mockMvc.perform(get("/api/test/hello")
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk());
    }
}
