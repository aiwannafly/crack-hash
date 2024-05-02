package ru.aiwannafly.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.aiwannafly.entities.worker.TaskRequest;

public interface TaskRequestRepository extends MongoRepository<TaskRequest, String> {
}
