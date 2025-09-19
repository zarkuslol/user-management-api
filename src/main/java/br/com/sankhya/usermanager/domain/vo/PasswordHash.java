package br.com.sankhya.usermanager.domain.vo;

import jakarta.validation.constraints.NotBlank;
import java.util.Objects;

public record PasswordHash(@NotBlank String hash) {
    public PasswordHash {
        Objects.requireNonNull(hash, "Password hash cannot be null");
    }
}
