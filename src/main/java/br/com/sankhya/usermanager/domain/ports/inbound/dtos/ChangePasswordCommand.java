package br.com.sankhya.usermanager.domain.ports.inbound.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordCommand(
        @Schema(description = "Senha atual do usuário.", example = "OldPassword@123", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank String oldPassword,

        @NotBlank @Size(min = 8, message = "New password must be at least 8 characters long") String newPassword
) {}
