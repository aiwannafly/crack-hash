package ru.aiwannafly.services;

import jakarta.annotation.Nonnull;
import ru.aiwannafly.entities.CrackRequest;
import ru.aiwannafly.entities.CrackResponse;
import ru.aiwannafly.entities.StatusResponse;

import java.util.List;

public interface CrackService {
    @Nonnull
    CrackResponse crack(@Nonnull CrackRequest request);

    StatusResponse status(@Nonnull String requestId);

    boolean update(@Nonnull String requestId, int partHumber, @Nonnull List<String> answers);
}
