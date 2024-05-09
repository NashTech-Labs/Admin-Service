package com.nashtech.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.core.ApiFuture;
import com.google.cloud.http.HttpTransportOptions;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.TopicName;
import com.nashtech.config.GCPConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.time.Instant;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

@Service

public class SaveUploadedResumesService {
    private final static Logger logger = LoggerFactory.getLogger(SaveUploadedResumesService.class);

    @Autowired
    private GCPConfig gcpConfig;
    @Autowired
    private ObjectMapper objectMapper;

    private static final String FILE_URL = "fileUrl";



    public HashMap<String, String> saveResumes(final MultipartFile file) {
        HashMap<String, String> messageMap = new HashMap<>();
        String uuid = String.valueOf(UUID.randomUUID());
        try {
            String path = uploadFileToGCSBucket(file.getOriginalFilename(), file);
            messageMap.put("candidateID",uuid);
            messageMap.put("path", path);
            messageMap.put("insertedTime", Instant.now().toString());
            messageMap.put("sourceSystem", "InternalStorage");
            publishFilePathToPubSub(objectMapper.writeValueAsString(messageMap));
            logger.info("Resume File Stored Successfully in GCS Bucket");
        } catch (Exception exception) {
            logger.info("Error in Storing the File : {}", exception.getMessage());
            messageMap.clear();
            messageMap.put("error", "Failed to store file");
            messageMap.put("exceptionMessage", exception.getMessage());
        }
        return messageMap;
    }

    private String uploadFileToGCSBucket(String objectName, MultipartFile file) {
        String baseUrl = "gs://";

        try {
            byte[] content = file.getBytes();
            String contentType = file.getContentType();

            HttpTransportOptions transportOptions = StorageOptions.getDefaultHttpTransportOptions();
            transportOptions = transportOptions.toBuilder()
                    .setConnectTimeout(60000)
                    .setReadTimeout(60000)
                    .build();
            Storage storage = StorageOptions.newBuilder()
                    .setTransportOptions(transportOptions)
                    .setProjectId(gcpConfig.getGcpProjectId())
                    .build()
                    .getService();

            BlobId blobId = BlobId.of(Objects.requireNonNull(gcpConfig.getGcpBucketName()), objectName);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                    .setContentType(contentType)
                    .build();

            try (ByteArrayInputStream byteStream = new ByteArrayInputStream(content)) {
                storage.createFrom(blobInfo, byteStream);
            }

            String fileUrl = baseUrl + gcpConfig.getGcpBucketName() + "/" + objectName;
            logger.info("File uploaded to URL: " + fileUrl);
            return fileUrl;

        } catch (Exception exception) {
            logger.error("Exception occurred while uploading file to GCS", exception);
            return "Error, Unable to store file in storage";
        }
    }



    private void publishFilePathToPubSub(final String message) {
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