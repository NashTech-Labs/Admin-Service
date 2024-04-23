package com.nashtech.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties("pubsub")
@Data
@Component
public class GCPConfig {
    private String projectId;
    private String topicId;
}
