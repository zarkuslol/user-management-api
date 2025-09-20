package br.com.sankhya.usermanager.domain.ports.inbound.dtos;

import jakarta.validation.constraints.NotBlank;

public record UpdateUserCommand(
        @NotBlank String username,
        String role
) {}
