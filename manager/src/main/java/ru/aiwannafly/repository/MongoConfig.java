package ru.aiwannafly.repository;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableMongoRepositories(basePackageClasses = {CrackInfoRepository.class, TaskRequestRepository.class})
@Configuration
public class MongoConfig {
}
