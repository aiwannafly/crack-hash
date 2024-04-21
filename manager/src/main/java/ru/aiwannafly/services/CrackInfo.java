package ru.aiwannafly.services;

import jakarta.annotation.Nonnull;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Setter
public class CrackInfo {
    @Id
    private String requestId;
    private List<List<String>> answers;

    public CrackInfo() {}

    public CrackInfo(@Nonnull String requestId, int partCount) {
        Objects.requireNonNull(requestId);

        this.requestId = requestId;
        this.answers = new ArrayList<>(partCount);
    }

    public String getRequestId() {
        return requestId;
    }

    public void addAnswers(int partNumber, @Nonnull List<String> answers) {
        Objects.requireNonNull(answers);

        synchronized (this) {
            this.answers.add(partNumber - 1, answers);
        }
    }

    boolean isReady() {
        return answers.stream().allMatch(Objects::nonNull);
    }

    public List<String> getAnswers() {
        return answers.stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }
}
