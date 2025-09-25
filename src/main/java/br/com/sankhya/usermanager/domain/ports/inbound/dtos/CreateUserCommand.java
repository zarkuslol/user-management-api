package br.com.sankhya.usermanager.domain.ports.inbound.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserCommand(
        @Schema(description = "Nome de usuário único para login.", example = "johndoe", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank String username,

        @Schema(description = "Endereço de e-mail único do usuário.", example = "john.doe@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank @Email String email,

        @Schema(description = "Senha do usuário. Mínimo de 8 caracteres.", example = "Password@123", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank @Size(min = 8, message = "Password must be at least 8 characters long") String password,

        @Schema(description = "Perfil de permissão do usuário. Ex: ADMIN, EDITOR, VIEWER", example = "VIEWER", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank String role
) {}
