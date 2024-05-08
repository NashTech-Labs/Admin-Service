package com.nashtech.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties
@Data
@Component
public class LLMConfig {

    @Value("${llm.api.resumeStructure}")
    private String resumeStructureUrl;

    @Value("${llm.api.topCandidates}")
    private String topCandidatesUrl;
}
