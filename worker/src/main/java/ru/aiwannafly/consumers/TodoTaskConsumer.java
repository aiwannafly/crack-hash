package ru.aiwannafly.consumers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.aiwannafly.entities.TaskRequest;
import ru.aiwannafly.services.TaskService;

import static ru.aiwannafly.RabbitConfig.TODO_TASKS_QUERY;

@EnableRabbit
@Component
public class TodoTaskConsumer {
    private static final Logger log = LoggerFactory.getLogger(TodoTaskConsumer.class);

    private final TaskService taskService;

    public TodoTaskConsumer(@Autowired TaskService taskService) {
        this.taskService = taskService;
    }

    @RabbitListener(queues = TODO_TASKS_QUERY)
    public void processTodoTask(TaskRequest taskRequest) {
        log.info(String.format("Got todo task with id: '%s'", taskRequest.getRequestId()));

        try {
            taskService.executeTask(taskRequest);
        } catch (Exception e) {
            log.error("Failed to process todo task.", e);
        }
    }
}
