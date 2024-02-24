package ru.aiwannafly.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nonnull;

import java.util.List;
import java.util.Objects;

public class StatusResponse {
    private final Status status;
    private final List<String> data;

    private StatusResponse(@Nonnull Status status, List<String> data) {
        Objects.requireNonNull(status);

        this.status = status;
        this.data = data;
    }

    public static StatusResponse inProgress() {
        return new StatusResponse(Status.IN_PROGRESS, null);
    }

    public static StatusResponse error() {
        return new StatusResponse(Status.ERROR, null);
    }

    public static  StatusResponse ready(@Nonnull List<String> data) {
        Objects.requireNonNull(data);

        return new StatusResponse(Status.READY, data);
    }

    @Nonnull
    public Status getStatus() {
        return status;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public List<String> getData() {
        return data;
    }
}
