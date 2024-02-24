package ru.aiwannafly.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.aiwannafly.entities.CrackRequest;
import ru.aiwannafly.entities.CrackResponse;
import ru.aiwannafly.entities.StatusResponse;
import ru.aiwannafly.services.CrackService;

import java.util.HashMap;
import java.util.Map;

@RestController
public class CrackController {
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

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errorByField = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String field = ((FieldError) error).getField();
            errorByField.put(field, error.getDefaultMessage());
        });
        return errorByField;
    }
}
