package com.nashtech.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.api.core.ApiFuture;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.TopicName;
import com.nashtech.config.GCPConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PubSubPublisherService {
    @Autowired
    private GCPConfig gcpConfig;
    private ObjectMapper objectMapper;


    public void messagePublisher(final String resume) {
        TopicName topicName = TopicName.of(gcpConfig.getTopicId(), gcpConfig.getTopicId());
        Publisher publisher;
        log.info("Publishing resume data to topic: {}", gcpConfig.getTopicId());

        try {
            publisher = Publisher.newBuilder(topicName).build();
            PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(ByteString.copyFromUtf8(resume)).build();
            ApiFuture<String> publishedMessage = publisher.publish(pubsubMessage);
            log.info("Message id generated:{}", publishedMessage.get());
        } catch (Exception exception) {
            log.error("Error : {} while publishing resume data to pub sub topic : {}", exception.getMessage(), gcpConfig.getTopicId());
        }
    }
}
