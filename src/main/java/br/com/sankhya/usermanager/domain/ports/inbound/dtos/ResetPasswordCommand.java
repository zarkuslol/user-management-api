package br.com.sankhya.usermanager.domain.ports.inbound.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordCommand(
        @NotBlank String token,
        @NotBlank @Size(min = 8) String newPassword
) {}
