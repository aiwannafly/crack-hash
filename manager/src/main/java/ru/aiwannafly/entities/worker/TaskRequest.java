package ru.aiwannafly.entities.worker;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.Objects;

public class TaskRequest {
    @NotBlank(message = "Request id must be non-empty")
    private final String requestId;
    @Positive(message = "Part number must be positive")
    private final int partNumber;
    @Positive(message = "Part count must be positive")
    private final int partCount;
    @NotBlank(message = "Hash must be non-empty")
    @Size(min = 32, max = 32, message = "Only MD5 hashes are accepted")
    private final String hash;
    @Positive(message = "Length must be positive")
    @Max(value = 6, message = "Length must be less or equal than 6")
    private final int maxLength;
    @NotBlank(message = "Alphabet must be non-empty")
    private final String alphabet;

    public TaskRequest(
            @Nonnull String requestId,
            int partNumber,
            int partCount,
            @Nonnull String hash,
            int maxLength,
            @Nonnull String alphabet
    ) {
        Objects.requireNonNull(requestId);
        Objects.requireNonNull(hash);
        Objects.requireNonNull(alphabet);

        this.requestId = requestId;
        this.partNumber = partNumber;
        this.partCount = partCount;
        this.hash = hash;
        this.maxLength = maxLength;
        this.alphabet = alphabet;
    }

    @Nonnull
    public String getRequestId() {
        return requestId;
    }

    public int getPartNumber() {
        return partNumber;
    }

    public int getPartCount() {
        return partCount;
    }

    @Nonnull
    public String getHash() {
        return hash;
    }

    public int getMaxLength() {
        return maxLength;
    }

    @Nonnull
    public String getAlphabet() {
        return alphabet;
    }
}
