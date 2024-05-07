package ru.aiwannafly.services;

import jakarta.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import ru.aiwannafly.ManagerConfig;
import ru.aiwannafly.entities.CrackRequest;
import ru.aiwannafly.entities.CrackResponse;
import ru.aiwannafly.entities.StatusResponse;
import ru.aiwannafly.entities.worker.TaskRequest;
import ru.aiwannafly.repository.CrackInfoRepository;
import ru.aiwannafly.repository.TaskRequestRepository;

import java.util.List;
import java.util.UUID;

import static ru.aiwannafly.RabbitConfig.TASKS_EXCHANGE;
import static ru.aiwannafly.RabbitConfig.TODO_KEY;

@Service
@EnableScheduling
public class DistributedCrackService implements CrackService {
    private static final Logger log = LoggerFactory.getLogger(DistributedCrackService.class);
    private final ManagerConfig managerConfig;
    private final CrackInfoRepository crackInfoRepository;
    private final TaskRequestRepository taskRequestRepository;
    private final AmqpTemplate rabbitTemplate;

    public DistributedCrackService(
            @Autowired ManagerConfig managerConfig,
            @Autowired CrackInfoRepository crackInfoRepository,
            @Autowired TaskRequestRepository taskRequestRepository,
            @Autowired AmqpTemplate rabbitTemplate
    ) {
        this.managerConfig = managerConfig;
        this.crackInfoRepository = crackInfoRepository;
        this.taskRequestRepository = taskRequestRepository;
        this.rabbitTemplate = rabbitTemplate;

        if (managerConfig.getAlphabet() == null) {
            log.error("Manager config does not contain alphabet.");
            throw new RuntimeException("Internal error.");
        }

        if (managerConfig.getPartCount() < 1) {
            log.error("Workers count must not be less then 1");
            throw new RuntimeException("Internal error.");
        }
    }

    @Nonnull
    @Override
    public synchronized CrackResponse crack(@Nonnull CrackRequest request) {
        String requestId = UUID.randomUUID().toString();

        int partCount = managerConfig.getPartCount();

        // save request
        crackInfoRepository.save(new CrackInfo(requestId, partCount));

        for (int partNumber = 1; partNumber <= partCount; partNumber++) {
            TaskRequest taskRequest = new TaskRequest(
                    requestId,
                    partNumber,
                    partCount,
                    request.getHash(),
                    request.getMaxLength(),
                    managerConfig.getAlphabet()
            );

            try {
                sendTaskToWorker(taskRequest);
            } catch (RuntimeException e) {
                log.error("Failed to send task to worker.", e);

                taskRequestRepository.save(taskRequest);
            }

            log.info(String.format("Sent task with request id = %s to worker.", requestId));
        }

        return new CrackResponse(requestId);
    }

    @Override
    public synchronized StatusResponse status(@Nonnull String requestId) {
        CrackInfo info = crackInfoRepository.findById(requestId).orElse(null);

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
    public synchronized boolean update(@Nonnull String requestId, int partHumber, @Nonnull List<String> answers) {
        log.info(String.format("Got answers: %s for request id = %s", answers, requestId));

        CrackInfo info = crackInfoRepository.findById(requestId).orElse(null);

        if (info == null)
            return false;

        info.addAnswers(partHumber, answers);
        crackInfoRepository.save(info);
        return true;
    }

    private synchronized void sendTaskToWorker(@Nonnull TaskRequest taskRequest) {
        rabbitTemplate.convertAndSend(TASKS_EXCHANGE, TODO_KEY, taskRequest);
    }

    @Scheduled(fixedDelay = 5000, initialDelay = 10000)
    private synchronized void checkStoredTasks() {
        List<TaskRequest> taskRequests = taskRequestRepository.findAll();

        if (taskRequests.isEmpty())
            return;

        log.info("Try to send stored tasks to workers...");

        for (TaskRequest taskRequest : taskRequests) {
            try {
                sendTaskToWorker(taskRequest);
            } catch (RuntimeException e) {
                log.error("Failed to send task to worker.", e);
                continue;
            }

            taskRequestRepository.delete(taskRequest);
        }
    }
}
