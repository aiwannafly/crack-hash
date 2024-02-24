package ru.aiwannafly.services;

import jakarta.annotation.Nonnull;
import ru.aiwannafly.entities.Status;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

public class CrackInfo {
    private Status status = Status.IN_PROGRESS;
    private final List<String> answers = new LinkedList<>();
    private final AtomicLong workerResponsesCounter = new AtomicLong();
    private final int partCount;

    public CrackInfo(int partCount) {
        this.partCount = partCount;
    }

    public void addAnswers(@Nonnull List<String> answers) {
        Objects.requireNonNull(answers);

        workerResponsesCounter.incrementAndGet();
        synchronized (this.answers) {
            this.answers.addAll(answers);
        }
    }

    boolean isReady() {
        return workerResponsesCounter.get() == partCount;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<String> getAnswers() {
        return answers;
    }
}
