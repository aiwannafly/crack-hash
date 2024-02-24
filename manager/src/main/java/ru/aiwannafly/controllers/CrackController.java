package ru.aiwannafly.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.aiwannafly.entities.CrackRequest;
import ru.aiwannafly.entities.CrackResponse;
import ru.aiwannafly.entities.StatusResponse;
import ru.aiwannafly.services.CrackService;

@RestController
public class CrackController extends ValidCheckController {
    private final CrackService crackService;

    public CrackController(@Autowired CrackService crackService) {
        this.crackService = crackService;
    }

    @PostMapping(value = "/api/hash/crack", consumes = "application/json", produces = "application/json")
    public CrackResponse crack(@Valid @RequestBody CrackRequest request) {
        return crackService.crack(request);
    }

    @GetMapping(value = "/api/hash/status")
    public StatusResponse status(@RequestParam String requestId) {
        StatusResponse response = crackService.status(requestId);

        if (response == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Request with this id is not found.");

        return response;
    }
}
