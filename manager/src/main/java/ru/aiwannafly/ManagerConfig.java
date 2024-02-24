package ru.aiwannafly;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "manager")
public class ManagerConfig {
    private String alphabet;
    private List<String> workerUrls;

    public List<String> getWorkerUrls() {
        return workerUrls;
    }

    public void setWorkerUrls(List<String> workerUrls) {
        this.workerUrls = workerUrls;
    }

    public String getAlphabet() {
        return alphabet;
    }

    public void setAlphabet(String alphabet) {
        this.alphabet = alphabet;
    }
}
