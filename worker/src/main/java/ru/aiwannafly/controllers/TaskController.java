package ru.aiwannafly.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.aiwannafly.entities.TaskRequest;
import ru.aiwannafly.services.TaskService;

@RestController
public class TaskController extends ValidCheckController {
    private final TaskService taskService;

    public TaskController(@Autowired TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping(value = "/internal/api/worker/hash/crack/task", consumes = "application/json", produces = "application/json")
    @ResponseStatus(value = HttpStatus.OK)
    public void task(@RequestBody @Valid TaskRequest request) {
        taskService.executeTask(request);
    }
}
