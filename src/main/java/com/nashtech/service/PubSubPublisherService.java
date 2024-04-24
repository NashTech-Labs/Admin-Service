package com.nashtech.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.core.ApiFuture;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.TopicName;
import com.nashtech.config.GCPConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PubSubPublisherService {
    final static Logger logger = LoggerFactory.getLogger(PubSubPublisherService.class);
    @Autowired
    private GCPConfig gcpConfig;
    private ObjectMapper objectMapper;


    public void messagePublisher(final String resume) {
        TopicName topicName = TopicName.of(gcpConfig.getTopicId(), gcpConfig.getTopicId());
        Publisher publisher;
        logger.info("Publishing resume data to topic: {}", gcpConfig.getTopicId());
        try {
            publisher = Publisher.newBuilder(topicName).build();
            PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(ByteString.copyFromUtf8(resume)).build();
            ApiFuture<String> publishedMessage = publisher.publish(pubsubMessage);
            logger.info("Message id generated:{}", publishedMessage.get());
        } catch (Exception exception) {
            logger.error("Error : {} while publishing resume data to pub sub topic : {}", exception.getMessage(), gcpConfig.getTopicId());
        }
    }
}
