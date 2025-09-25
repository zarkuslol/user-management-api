package br.com.sankhya.usermanager.domain.ports.inbound.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record UpdateUserCommand(
        @Schema(description = "Novo nome de usuário.", example = "john.doe.new", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank String username,

        @Schema(description = "Novo perfil de permissão do usuário.", example = "EDITOR")
        String role
) {}
