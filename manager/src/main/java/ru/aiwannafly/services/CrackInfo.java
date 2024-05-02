package ru.aiwannafly.services;

import jakarta.annotation.Nonnull;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.util.*;

@Setter
public class CrackInfo {
    @Id
    private String requestId;
    private Set<Integer> receivedParts;
    private Set<String> answers;
    private int partCount;

    public CrackInfo() {}

    public CrackInfo(@Nonnull String requestId, int partCount) {
        Objects.requireNonNull(requestId);

        this.partCount = partCount;
        this.requestId = requestId;
        this.answers = new HashSet<>();
        this.receivedParts = new HashSet<>();
    }

    public String getRequestId() {
        return requestId;
    }

    public void addAnswers(int partNumber, @Nonnull List<String> answers) {
        Objects.requireNonNull(answers);

        synchronized (this) {
            this.receivedParts.add(partNumber);
            this.answers.addAll(answers);
        }
    }

    boolean isReady() {
        return receivedParts.size() == partCount;
    }

    public List<String> getAnswers() {
        return new ArrayList<>(answers);
    }
}
