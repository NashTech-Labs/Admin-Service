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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

@Service
@AllArgsConstructor
public class SaveUploadedResumesService {
    private final static Logger logger = LoggerFactory.getLogger(SaveUploadedResumesService.class);
    private final Path fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();
    @Autowired
    private GCPConfig gcpConfig;
    private ObjectMapper objectMapper;

    public void saveResumes(final MultipartFile file) {
        String uuid = String.valueOf(UUID.randomUUID());
        try {
            Files.createDirectories(this.fileStorageLocation);
            Path targetLocation = this.fileStorageLocation.resolve(Objects.requireNonNull(file.getOriginalFilename()));
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            String path = sendToGCSBucket(targetLocation, file.getOriginalFilename());
            HashMap<String, String> messageMap = new HashMap<>();
            messageMap.put("candidateID",uuid);
            messageMap.put("path", path);
            messageMap.put("insertedTime", Instant.now().toString());
            messageMap.put("sourceSystem", "InternalStorage");
            pathPublisher(objectMapper.writeValueAsString(messageMap));
            logger.info("Resume File Stored Successfully in GCS Bucket");
        } catch (Exception e) {
            logger.info("Error in Storing the File : {}", e.getMessage());
        }
    }

    private String sendToGCSBucket(final Path targetLocation, final String objectName) throws Exception {
        System.out.println("method 1");
        Storage storage = StorageOptions.newBuilder().setProjectId(gcpConfig.getProjectId()).build().getService();
        System.out.println("Hello---------------");
        BlobId blobId = BlobId.of(gcpConfig.getBucketName(), objectName);
        System.out.println("---------------------Hello---------------");
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        try {
            storage.createFrom(blobInfo, Paths.get(targetLocation.toUri()));
        } catch (Exception e) {
            throw new Exception(e);
        }
        logger.info(
                "File " + targetLocation + " uploaded to bucket " + gcpConfig.getBucketName() + " as " + objectName);
        return (gcpConfig.getBucketName() + objectName);
    }

    private void pathPublisher(final String message) {
        TopicName topicName = TopicName.of(gcpConfig.getProjectId(), gcpConfig.getTopicId());
        Publisher publisher;
        logger.info("Publishing resume data to topic: {}", gcpConfig.getTopicId());
        try {
            publisher = Publisher.newBuilder(topicName).build();
            PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(ByteString.copyFromUtf8(message)).build();
            ApiFuture<String> publishedMessage = publisher.publish(pubsubMessage);
            logger.info("Message id generated:{}", publishedMessage.get());
        } catch (Exception exception) {
            logger.error("Error : {} while publishing resume data to pub sub topic : {}", exception.getMessage(), gcpConfig.getTopicId());
        }
    }

}
