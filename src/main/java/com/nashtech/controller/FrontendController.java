package com.nashtech.controller;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.nashtech.config.GCPConfig;
import com.nashtech.service.ReadFromResumesServices;
import com.nashtech.service.SaveUploadedResumesService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/resumes")
@AllArgsConstructor
public class FrontendController {
    final static Logger logger = LoggerFactory.getLogger(FrontendController.class);
    @Autowired
    private final ReadFromResumesServices readFromResumesServices;
    @Autowired
    private final SaveUploadedResumesService saveUploadedResumesService;
    @Autowired
    private final GCPConfig gcpConfig;

    @GetMapping("/report")
    public String getMessage(){
        return "Hello";
    }

//    @PostMapping("/upload")
//    public ResponseEntity<String> getResumesInPDFFormat(@RequestParam("file") MultipartFile file) {
//        if (file.isEmpty()) {
//            return ResponseEntity.badRequest().body("No file uploaded.");
//        }
//        try {
//            String resumeData = readFromResumesServices.readFromUploadedResumeFile(file);
//            logger.info("Writing Data in Pub/Sub Topic");
//            saveUploadedResumesService.saveResumes(file);
//            return ResponseEntity.ok("Resume File uploaded");
//        } catch (IOException e) {
//            return ResponseEntity.internalServerError().body("Error processing PDF file: " + e.getMessage());
//        }
//    }

    private final Storage storage = StorageOptions.getDefaultInstance().getService();

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // Get the original filename
            String originalFilename = file.getOriginalFilename();

            // Prepare the blobId
            BlobId blobId = BlobId.of(gcpConfig.getBucketName(), originalFilename);

            // Prepare the blob information
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();

            // Upload the file to Google Cloud Storage
            storage.create(blobInfo, file.getBytes());

            return "File uploaded successfully: " + originalFilename;
        } catch (Exception e) {
            e.printStackTrace();
            return "The file upload failed: " + e.getMessage();
        }
    }
}