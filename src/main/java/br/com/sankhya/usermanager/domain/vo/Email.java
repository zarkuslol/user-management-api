package br.com.sankhya.usermanager.domain.vo;

import jakarta.validation.constraints.NotBlank;
import java.util.Objects;

public record Email(@NotBlank String address) {
    public Email {
        Objects.requireNonNull(address, "Email address cannot be null");
        if (!address.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }
}
