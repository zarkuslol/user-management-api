package br.com.sankhya.usermanager.infrastructure.adapters.inbound.rest;

import br.com.sankhya.usermanager.domain.ports.inbound.AuthUseCase;
import br.com.sankhya.usermanager.domain.ports.inbound.dtos.LoginResponse;
import br.com.sankhya.usermanager.infrastructure.adapters.inbound.rest.handlers.GlobalExceptionHandler;
import br.com.sankhya.usermanager.infrastructure.config.WebSecurityConfig; // Importe o WebSecurityConfig
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf; // Importe o CSRF helper
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import({WebSecurityConfig.class, GlobalExceptionHandler.class}) // Importamos a segurança e o handler
class AuthControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthUseCase authUseCase;

    // Eles são necessários para que o WebSecurityConfig possa ser construído.
    @MockBean
    private br.com.sankhya.usermanager.domain.ports.outbound.TokenServicePort tokenServicePort;
    @MockBean
    private br.com.sankhya.usermanager.domain.ports.outbound.UserRepositoryPort userRepositoryPort;
    @MockBean
    private br.com.sankhya.usermanager.infrastructure.adapters.inbound.security.CustomAccessDeniedHandler customAccessDeniedHandler;

    @Test
    @DisplayName("POST /auth/login should return 200 OK and JWT on successful authentication")
    void login_WithValidCredentials_ShouldReturnOkAndToken() throws Exception {
        var loginCommandJson = "{ \"username\": \"dev\", \"password\": \"dev123\" }";
        var loginResponse = new LoginResponse("fake.jwt.token");

        when(authUseCase.login(any())).thenReturn(loginResponse);

        mockMvc.perform(post("/auth/login")
                        .with(csrf()) // Adicione o CSRF, pois a segurança está ativa
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginCommandJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").value("fake.jwt.token"));
    }

    @Test
    @DisplayName("POST /auth/login should return 401 Unauthorized on authentication failure")
    void login_WithInvalidCredentials_ShouldReturnUnauthorized() throws Exception {
        var loginCommandJson = "{ \"username\": \"dev\", \"password\": \"wrongpass\" }";

        when(authUseCase.login(any()))
                .thenThrow(new BadCredentialsException("Invalid username or password"));

        mockMvc.perform(post("/auth/login")
                        .with(csrf()) // Adicione o CSRF
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginCommandJson))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Invalid username or password"));
    }

    @Test
    @DisplayName("POST /auth/login should return 400 Bad Request for invalid input")
    void login_WithMissingPassword_ShouldReturnBadRequest() throws Exception {
        var invalidLoginCommandJson = "{ \"username\": \"dev\" }";

        mockMvc.perform(post("/auth/login")
                        .with(csrf()) // Adicione o CSRF
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidLoginCommandJson))
                .andExpect(status().isBadRequest());
    }
}
