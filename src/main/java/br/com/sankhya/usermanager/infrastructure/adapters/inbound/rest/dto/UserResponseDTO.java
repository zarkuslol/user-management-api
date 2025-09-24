package br.com.sankhya.usermanager.infrastructure.adapters.inbound.rest.dto;

import br.com.sankhya.usermanager.domain.model.User;
import java.time.LocalDateTime;

public record UserResponseDTO(
        Long id,
        String username,
        String email,
        String role,
        boolean enabled,
        LocalDateTime createdAt,
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
