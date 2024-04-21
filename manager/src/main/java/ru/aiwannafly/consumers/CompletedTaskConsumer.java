package ru.aiwannafly.consumers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.aiwannafly.entities.worker.TaskResponse;
import ru.aiwannafly.services.CrackService;

import static ru.aiwannafly.RabbitConfig.COMPLETED_TASKS_QUERY;

@EnableRabbit
@Component
public class CompletedTaskConsumer {
    private static final Logger log = LoggerFactory.getLogger(CompletedTaskConsumer.class);
    private final CrackService crackService;

    public CompletedTaskConsumer(@Autowired CrackService crackService) {
        this.crackService = crackService;
    }

    @RabbitListener(queues = COMPLETED_TASKS_QUERY)
    public void processCompletedTask(TaskResponse taskResponse) {
        log.info("Received from completed-tasks the id: " + taskResponse.requestId());

        try {
            boolean updated = crackService.update(
                    taskResponse.requestId(),
                    taskResponse.partNumber(),
                    taskResponse.answers()
            );

            if (!updated)
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found request with the id.");
        } catch (Exception e) {
            log.error("Failed to process completed task.", e);
        }
    }
}
