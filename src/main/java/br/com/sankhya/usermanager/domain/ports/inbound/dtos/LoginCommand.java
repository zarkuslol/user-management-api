package br.com.sankhya.usermanager.domain.ports.inbound.dtos;

import jakarta.validation.constraints.NotBlank;

public record LoginCommand(
        @NotBlank String username,
        @NotBlank String password
) {}
