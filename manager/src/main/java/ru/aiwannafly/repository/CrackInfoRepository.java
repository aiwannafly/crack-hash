package ru.aiwannafly.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.aiwannafly.services.CrackInfo;

public interface CrackInfoRepository extends MongoRepository<CrackInfo, String> {
}
