package br.com.sankhya.usermanager.domain.ports.inbound.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordCommand(
        @NotBlank String oldPassword,
        @NotBlank @Size(min = 8, message = "New password must be at least 8 characters long") String newPassword
) {}
