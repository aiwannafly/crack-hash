package ru.aiwannafly.services;

import jakarta.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import ru.aiwannafly.ManagerConfig;
import ru.aiwannafly.entities.CrackRequest;
import ru.aiwannafly.entities.CrackResponse;
import ru.aiwannafly.entities.StatusResponse;
import ru.aiwannafly.entities.worker.TaskRequest;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SingleWorkerCrackService implements CrackService {
    private static final Logger log = LoggerFactory.getLogger(SingleWorkerCrackService.class);
    private final ManagerConfig managerConfig;
    private final Map<String, CrackInfo> statusByRequestId = new ConcurrentHashMap<>();

    public SingleWorkerCrackService(@Autowired ManagerConfig managerConfig) {
        this.managerConfig = managerConfig;

        if (CollectionUtils.isEmpty(managerConfig.getWorkerUrls())) {
            log.error("Manager config does not contain any worker url.");
            throw new RuntimeException("Internal error.");
        }

        if (managerConfig.getAlphabet() == null) {
            log.error("Manager config does not contain alphabet.");
            throw new RuntimeException("Internal error.");
        }
    }

    @Nonnull
    @Override
    public CrackResponse crack(@Nonnull CrackRequest request) {
        String requestId = UUID.randomUUID().toString();

        TaskRequest taskRequest = new TaskRequest(
                requestId, 1, 1, request.getHash(), // single worker
                request.getMaxLength(), managerConfig.getAlphabet()
        );

        sendTaskToWorker(managerConfig.getWorkerUrls().get(0),taskRequest);

        log.info(String.format("Sent task with request id = %s to worker.", requestId));

        // call workers
        CrackInfo info = new CrackInfo(1); // single worker
        statusByRequestId.put(requestId, info);

        return new CrackResponse(requestId);
    }

    @Override
    public StatusResponse status(@Nonnull String requestId) {
        CrackInfo info = statusByRequestId.get(requestId);

        if (info == null)
            return null;

        if (info.isReady()) {
            if (CollectionUtils.isEmpty(info.getAnswers()))
                return StatusResponse.error();

            return StatusResponse.ready(info.getAnswers());
        }

        return StatusResponse.inProgress();
    }

    @Override
    public boolean update(@Nonnull String requestId, @Nonnull List<String> answers) {
        log.info(String.format("Got answers: %s for request id = %s", answers, requestId));

        CrackInfo info = statusByRequestId.get(requestId);

        if (info == null)
            return false;

        info.addAnswers(answers);
        return true;
    }

    private void sendTaskToWorker(
            @Nonnull String workerUrl,
            @Nonnull TaskRequest taskRequest
    ) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            String url = workerUrl + "/internal/api/worker/hash/crack/task";

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<TaskRequest> request = new HttpEntity<>(taskRequest, httpHeaders);

            restTemplate.postForLocation(url, request);
        } catch (RuntimeException e) {
            log.error("Failed to send task to worker.", e);
        }
    }
}
