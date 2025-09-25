package br.com.sankhya.usermanager.infrastructure.adapters.inbound.rest.dto;

import java.time.LocalDateTime;

public record ErrorResponseDTO(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path
) {}
