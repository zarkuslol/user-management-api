package br.com.sankhya.usermanager.domain.ports.inbound.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginResponse(
        @Schema(description = "Token de autenticação JWT.", example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIi...")
        String token
) {}
