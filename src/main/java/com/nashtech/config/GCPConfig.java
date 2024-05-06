package com.nashtech.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties("pubsub")
@Data
@Component
public class GCPConfig {
    @Value("${gcp.project.id}")
    private String gcpProjectId;

    @Value("${gcp.bucket.name}")
    private String gcpBucketName;

    @Value("${gcp.topic.id}")
    private String gcpTopicId;
}
