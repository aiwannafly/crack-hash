package ru.aiwannafly.entities;

import jakarta.annotation.Nonnull;

import java.util.Objects;

public class CrackResponse {
    private final String requestId;

    public CrackResponse(@Nonnull String requestId) {
        Objects.requireNonNull(requestId);

        this.requestId = requestId;
    }

    @Nonnull
    public String getRequestId() {
        return requestId;
    }
}
