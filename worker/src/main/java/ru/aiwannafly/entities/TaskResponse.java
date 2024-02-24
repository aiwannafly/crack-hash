package ru.aiwannafly.entities;

import jakarta.annotation.Nonnull;

import java.util.List;
import java.util.Objects;

public class TaskResponse {
    private final String requestId;
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
