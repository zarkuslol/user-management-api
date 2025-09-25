package br.com.sankhya.usermanager.domain.ports.inbound.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UpdateUserEmailCommand(
        @Schema(description = "Novo endereço de e-mail.", example = "john.doe.new@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank @Email String newEmail
) {}
