package com.nashtech.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.core.ApiFuture;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.TopicName;
import com.nashtech.config.GCPConfig;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HashMap;
import java.util.UUID;

@Service
@AllArgsConstructor
public class SaveUploadedResumesService {
    private final static Logger logger = LoggerFactory.getLogger(SaveUploadedResumesService.class);

    @Autowired
    private GCPConfig gcpConfig;
    private ObjectMapper objectMapper;

    public void saveResumes(final MultipartFile file, String resumeData) {
        String uuid = String.valueOf(UUID.randomUUID());
        try {
            //String path = sendToGCSBucket(file.getOriginalFilename(), resumeData);
            HashMap<String, String> messageMap = new HashMap<>();
            messageMap.put("candidateID",uuid);
            messageMap.put("path", "hello");
            messageMap.put("insertedTime", Instant.now().toString());
            messageMap.put("sourceSystem", "InternalStorage");
            pathPublisher(objectMapper.writeValueAsString(messageMap));
            logger.info("Resume File Stored Successfully in GCS Bucket");
        } catch (Exception e) {
            logger.info("Error in Storing the File : {}", e.getMessage());
        }
    }

    private String sendToGCSBucket(String objectName, String resumeData) {
        try {
            System.out.println("line 57");
            Storage storage = StorageOptions.newBuilder().setProjectId(gcpConfig.getGcpProjectId()).build().getService();
            System.out.println("line 59");
            BlobId blobId = BlobId.of(gcpConfig.getGcpBucketName(), objectName);
            System.out.println("line 61");
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("application/pdf").build();
            System.out.println("line 63");
            storage.create(blobInfo, resumeData.getBytes(StandardCharsets.UTF_8));
            logger.info("uploaded to bucket " + gcpConfig.getGcpBucketName() + " as " + objectName);
            return (gcpConfig.getGcpBucketName() + objectName);
        } catch (Exception exception) {
            logger.info("Exception Occurred : "+ exception.getMessage());
            return ("Error");
        }
    }


    private void pathPublisher(final String message) {
        TopicName topicName = TopicName.of(gcpConfig.getGcpProjectId(), gcpConfig.getGcpTopicId());
        Publisher publisher;
        logger.info("Publishing resume data to topic: {}", gcpConfig.getGcpTopicId());
        try {
            publisher = Publisher.newBuilder(topicName).build();
            PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(ByteString.copyFromUtf8(message)).build();
            ApiFuture<String> publishedMessage = publisher.publish(pubsubMessage);
            logger.info("Message id generated:{}", publishedMessage.get());
        } catch (Exception exception) {
            logger.error("Error : {} while publishing resume data to pub sub topic : {}", exception.getMessage(), gcpConfig.getGcpTopicId());
        }
    }

}
