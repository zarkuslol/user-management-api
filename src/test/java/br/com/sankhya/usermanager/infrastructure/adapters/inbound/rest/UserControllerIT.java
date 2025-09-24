package br.com.sankhya.usermanager.infrastructure.adapters.inbound.rest;

import br.com.sankhya.usermanager.domain.model.Role;
import br.com.sankhya.usermanager.domain.model.User;
import br.com.sankhya.usermanager.domain.ports.inbound.UserManagementUseCase;
import br.com.sankhya.usermanager.domain.vo.Email;
import br.com.sankhya.usermanager.infrastructure.config.WebSecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(WebSecurityConfig.class)
class UserControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserManagementUseCase userManagementUseCase;

    // Mocks necessários para o WebSecurityConfig carregar
    @MockBean
    private br.com.sankhya.usermanager.domain.ports.outbound.TokenServicePort tokenServicePort;
    @MockBean
    private br.com.sankhya.usermanager.domain.ports.outbound.UserRepositoryPort userRepositoryPort;
    @MockBean
    private br.com.sankhya.usermanager.infrastructure.adapters.inbound.security.CustomAccessDeniedHandler customAccessDeniedHandler;

    @Test
    @DisplayName("POST /users should create user and return 201 Created")
    @WithMockUser(roles = "ADMIN") // Garante que o usuário tem permissão para criar
    void createUser_ShouldReturn201Created() throws Exception {
        // Arrange
        // O DTO que esperamos enviar no corpo da requisição
        var commandJson = """
                    {
                        "username": "newuser",
                        "email": "new.user@example.com",
                        "password": "password123",
                        "role": "VIEWER"
                    }
                """;

        // O objeto de domínio que nosso mock do UseCase vai retornar
        User createdUser = new User();
        createdUser.setId(99L); // ID de exemplo
        createdUser.setUsername("newuser");
        createdUser.setEmail(new Email("new.user@example.com")); // E-mail correspondente
        createdUser.setRole(new Role(2L, "VIEWER"));          // Role correspondente
        createdUser.setCreatedAt(LocalDateTime.now());
        createdUser.setUpdatedAt(LocalDateTime.now());

        // Configuramos o mock para a operação de CRIAR
        when(userManagementUseCase.createUser(any())).thenReturn(createdUser);

        // Act & Assert
        // AGORA SIM: fazemos um POST para /users e verificamos a criação
        mockMvc.perform(post("/users")
                        .with(csrf()) // Necessário para POST com Spring Security
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(commandJson))
                .andExpect(status().isCreated()) // Esperamos 201 Created
                .andExpect(header().string("Location", "http://localhost/users/99"));
    }

    @Test
    @DisplayName("GET /users/{id} should return user when found")
    @WithMockUser
    void findUserById_WhenFound_ShouldReturnUser() throws Exception {
        // Arrange
        // O objeto de domínio COMPLETO que nosso mock do UseCase vai retornar
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail(new Email("test@example.com")); // Adicionado
        user.setRole(new Role(1L, "ADMIN"));          // Adicionado
        user.setCreatedAt(LocalDateTime.now());       // Adicionado
        user.setUpdatedAt(LocalDateTime.now());       // Adicionado

        when(userManagementUseCase.findUserById(1L)).thenReturn(Optional.of(user));

        // Act & Assert
        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com")); // Assert novo e útil
    }

    @Test
    @DisplayName("GET /users/{id} should return 404 Not Found when not found")
    @WithMockUser
    void findUserById_WhenNotFound_ShouldReturn404() throws Exception {
        when(userManagementUseCase.findUserById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /users/{id} should update user and return 200 OK")
    @WithMockUser(roles = "ADMIN")
    void updateUser_ShouldReturn200Ok() throws Exception {
        // Arrange
        var commandJson = "{ \"username\": \"updateduser\", \"role\": \"EDITOR\" }";

        // O objeto de domínio COMPLETO que nosso mock do UseCase vai retornar
        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setUsername("updateduser");
        updatedUser.setEmail(new Email("updated@example.com")); // Adicionado
        updatedUser.setRole(new Role(3L, "EDITOR"));          // Adicionado e corrigido
        updatedUser.setCreatedAt(LocalDateTime.now());       // Adicionado
        updatedUser.setUpdatedAt(LocalDateTime.now());       // Adicionado

        when(userManagementUseCase.updateUserProfile(any(), any())).thenReturn(Optional.of(updatedUser));

        // Act & Assert
        mockMvc.perform(put("/users/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(commandJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("updateduser"))
                .andExpect(jsonPath("$.role").value("EDITOR")); // Podemos adicionar um assert novo
    }

    @Test
    @DisplayName("DELETE /users/{id} should return 204 No Content")
    @WithMockUser(roles = "ADMIN")
    void deleteUser_ShouldReturn204() throws Exception {
        doNothing().when(userManagementUseCase).deleteUserById(1L);

        mockMvc.perform(delete("/users/1").with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("PATCH /users/{id}/activate should return 204 No Content")
    @WithMockUser(roles = "ADMIN")
    void activateUser_ShouldReturn204() throws Exception {
        doNothing().when(userManagementUseCase).activateUser(1L);

        mockMvc.perform(patch("/users/1/activate").with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /users/{id} should return 204 No Content when user exists")
    @WithMockUser(roles = "ADMIN") // Apenas ADMINS podem deletar usuários
    void deleteUser_WhenUserExists_ShouldReturn204NoContent() throws Exception {
        // Arrange (Organizar)
        // Configuramos o mock para não fazer nada (void) quando o método for chamado com o ID 1.
        doNothing().when(userManagementUseCase).deleteUserById(1L);

        // Act & Assert (Agir e Verificar)
        mockMvc.perform(delete("/users/1")
                        .with(csrf())) // Adiciona o token CSRF
                .andExpect(status().isNoContent()); // Esperamos o status 204 No Content
    }

    @Test
    @DisplayName("DELETE /users/{id} should do nothing when user does not exist")
    @WithMockUser(roles = "ADMIN")
    void deleteUser_WhenUserDoesNotExist_ShouldAlsoReturn204NoContent() throws Exception {
        // Arrange
        // A lógica de negócio decide não retornar erro para não dar pistas sobre quais IDs existem.
        // O método simplesmente não fará nada.
        doNothing().when(userManagementUseCase).deleteUserById(999L);

        // Act & Assert
        mockMvc.perform(delete("/users/999").with(csrf()))
                .andExpect(status().isNoContent());
    }

}
