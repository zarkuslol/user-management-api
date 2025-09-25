package br.com.sankhya.usermanager.infrastructure.adapters.inbound.rest.dto;

import br.com.sankhya.usermanager.domain.model.User;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record UserResponseDTO(
        @Schema(description = "ID único do usuário.", example = "1")
        Long id,

        @Schema(description = "Nome de usuário.", example = "johndoe")
        String username,

        @Schema(description = "Endereço de e-mail do usuário.", example = "john.doe@example.com")
        String email,

        @Schema(description = "Perfil de permissão.", example = "VIEWER")
        String role,

        @Schema(description = "Indica se o usuário está ativo.", example = "true")
        boolean enabled,

        @Schema(description = "Data e hora de criação do registro.")
        LocalDateTime createdAt,

        @Schema(description = "Data e hora da última atualização.")
        LocalDateTime updatedAt
) {
    // Método de fábrica para converter nosso modelo de domínio para o DTO
    public static UserResponseDTO fromDomain(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail().address(),
                user.getRole() != null ? user.getRole().getName() : null,
                user.isEnabled(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
