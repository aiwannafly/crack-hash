package ru.aiwannafly.entities;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.Objects;

public class CrackRequest {
    @NotBlank(message = "Hash must be non-empty")
    @Size(min = 32, max = 32, message = "Only MD5 hashes are accepted")
    private final String hash;
    @Positive(message = "Length must be positive")
    @Max(value = 6, message = "Length must be less or equal than 6")
    private final int maxLength;

    public CrackRequest(@Nonnull String hash, int maxLength) {
        Objects.requireNonNull(hash);

        this.hash = hash;
        this.maxLength = maxLength;
    }

    @Nonnull
    public String getHash() {
        return hash;
    }

    public int getMaxLength() {
        return maxLength;
    }
}
