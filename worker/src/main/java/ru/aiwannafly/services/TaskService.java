package ru.aiwannafly.services;

import jakarta.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import ru.aiwannafly.WorkerConfig;
import ru.aiwannafly.entities.TaskRequest;
import ru.aiwannafly.entities.TaskResponse;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static ru.aiwannafly.RabbitConfig.COMPLETED_KEY;
import static ru.aiwannafly.RabbitConfig.TASKS_EXCHANGE;

@Service
public class TaskService {
    private final ExecutorService executorService = Executors.newFixedThreadPool(1);
    private static final Logger log = LoggerFactory.getLogger(TaskService.class);
    private final WorkerConfig workerConfig;
    private final AmqpTemplate rabbitTemplate;

    public TaskService(@Autowired WorkerConfig workerConfig, @Autowired AmqpTemplate rabbitTemplate) {
        this.workerConfig = workerConfig;
        this.rabbitTemplate = rabbitTemplate;

        if (workerConfig.getManagerUrl() == null) {
            log.error("Worker config does not contain manager url.");

            throw new RuntimeException("Internal error.");
        }
    }

    public void executeTask(@Nonnull TaskRequest request) {
        executorService.submit(() -> {
            String alphabet = request.getAlphabet();

            // Each worker should try some part of string combinations.
            // The split is implemented via first symbol of a string.
            // E.g. if we have 3 workers, then the first worker will consider
            // the 1/3 of alphabet for the first symbol, the second — 2/3 and so on.
            String firstSymbolAlphabet = getFirstSymbolAlphabet(request, alphabet);

            List<byte[]> symbolAlphabets = new ArrayList<>(request.getMaxLength());

            symbolAlphabets.add(0, firstSymbolAlphabet.getBytes(StandardCharsets.UTF_8));
            for (int i = 1; i < request.getMaxLength(); i++)
                symbolAlphabets.add(i, alphabet.getBytes(StandardCharsets.UTF_8));

            List<String> matches = findMatches(symbolAlphabets, request.getHash().toLowerCase());

            log.info(String.format("Found the matches: %s.", matches));

            var response = new TaskResponse(request.getRequestId(), request.getPartNumber(), matches);
            sentResponseToManager(response);
        });
    }

    private void sentResponseToManager(@Nonnull TaskResponse response) {
        try {
            rabbitTemplate.convertAndSend(TASKS_EXCHANGE, COMPLETED_KEY, response);
        } catch (RuntimeException e) {
            log.error("Failed to send response to manager.", e);
        }
    }

    @Nonnull
    private List<String> findMatches(@Nonnull List<byte[]> symbolAlphabets, @Nonnull String desiredHash) {
        List<String> matches = new LinkedList<>();

        for (int seqLength = 1; seqLength <= symbolAlphabets.size(); seqLength++) {

            int[] alphabetPositions = new int[seqLength]; // each symbol position in its alphabet
            byte[] symbols = new byte[seqLength];

            /*
            0 0 0
            0 0 1
            0 1 0
            0 1 1
            1 0 0
            1 0 1
            1 1 0
            1 1 1
            */

            mainLoop:
            while (true) {
                // calculate hash for the current sequence

                String hash = DigestUtils.md5DigestAsHex(symbols);

                if (hash.equals(desiredHash))
                    matches.add(new String(symbols, StandardCharsets.UTF_8));

                for (int i = 0; i < seqLength; i++) {
                    alphabetPositions[i] += 1;

                    if (alphabetPositions[i] == symbolAlphabets.get(i).length) {
                        // overflow
                        alphabetPositions[i] = 0;

                        if (i == seqLength - 1)
                            break mainLoop;
                    } else {
                        break;
                    }
                }

                for (int i = 0; i < seqLength; i++)
                    symbols[i] = symbolAlphabets.get(i)[alphabetPositions[i]];
            }
        }
        return matches;
    }

    @Nonnull
    private static String getFirstSymbolAlphabet(@Nonnull TaskRequest request, @Nonnull String alphabet) {
        int alphabetLength = alphabet.length();

        if (alphabetLength < request.getPartCount())
            throw new IllegalArgumentException("Too many parts count, expected a value less than alphabet length.");

        int begin = (request.getPartNumber() - 1) * alphabetLength / request.getPartCount();
        int end = request.getPartNumber() * alphabetLength / request.getPartCount();

        return alphabet.substring(begin, end);
    }
}
