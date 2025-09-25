package br.com.sankhya.usermanager.domain.ports.inbound.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record LoginCommand(

        @Schema(description = "Nome de usuário para login.", example = "johndoe", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank String username,

        @Schema(description = "Senha do usuário.", example = "Password@123", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank String password
) {}
