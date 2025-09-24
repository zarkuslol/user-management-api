package br.com.sankhya.usermanager.domain.ports.inbound.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordCommand(
        @NotBlank @Email String email
) {}
