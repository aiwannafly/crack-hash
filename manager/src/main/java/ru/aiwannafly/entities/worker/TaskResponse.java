package ru.aiwannafly.entities.worker;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.util.List;
import java.util.Objects;

public record TaskResponse(
        @NotBlank(message = "Request id must be non-empty") String requestId,
        @Positive(message = "Part number must be positive") int partNumber,
        List<String> answers
) {
    public TaskResponse(
            @Nonnull String requestId,
            int partNumber,
            @Nonnull List<String> answers
    ) {
        Objects.requireNonNull(requestId);
        Objects.requireNonNull(answers);

        this.requestId = requestId;
        this.partNumber = partNumber;
        this.answers = answers;
    }

    @Override
    @Nonnull
    public String requestId() {
        return requestId;
    }

    @Override
    public int partNumber() {
        return partNumber;
    }

    @Override
    @Nonnull
    public List<String> answers() {
        return answers;
    }
}
