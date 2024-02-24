package ru.aiwannafly.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import ru.aiwannafly.entities.worker.TaskResponse;
import ru.aiwannafly.services.CrackService;

@RestController
public class TaskController extends ValidCheckController {
    private final CrackService crackService;

    public TaskController(@Autowired CrackService crackService) {
        this.crackService = crackService;
    }

    @PostMapping(value = "/internal/api/manager/hash/crack/request")
    @ResponseStatus(HttpStatus.OK)
    public void handleWorkerResponse(@RequestBody @Valid TaskResponse taskResponse) {
        boolean updated = crackService.update(taskResponse.getRequestId(), taskResponse.getAnswers());

        if (!updated)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found request with the id.");
    }
}
