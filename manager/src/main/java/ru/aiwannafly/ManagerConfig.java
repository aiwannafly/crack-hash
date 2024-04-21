package ru.aiwannafly;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "manager")
public class ManagerConfig {
    private String alphabet;

    public String getAlphabet() {
        return alphabet;
    }

    public void setAlphabet(String alphabet) {
        this.alphabet = alphabet;
    }
}
