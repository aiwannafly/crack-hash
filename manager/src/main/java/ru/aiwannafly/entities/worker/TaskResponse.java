package ru.aiwannafly.entities.worker;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.util.List;
import java.util.Objects;

public class TaskResponse {
    @NotBlank(message = "Request id must be non-empty")
    private final String requestId;
    @Positive(message = "Part number must be positive")
    private final int partNumber;
    private final List<String> answers;

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

    @Nonnull
    public String getRequestId() {
        return requestId;
    }

    public int getPartNumber() {
        return partNumber;
    }

    @Nonnull
    public List<String> getAnswers() {
        return answers;
    }
}
