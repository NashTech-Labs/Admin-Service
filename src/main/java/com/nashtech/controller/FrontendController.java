package com.nashtech.controller;

import com.nashtech.service.PubSubPublisherService;
import com.nashtech.service.ReadFromResumesServices;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
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
    private final PubSubPublisherService pubSubPublisherService;
    @Autowired
    private final ReadFromResumesServices readFromResumesServices;

    @GetMapping("/report")
    public String getMessage(){
        return "Hello";
    }

    @PostMapping("/upload")
    public ResponseEntity<String> getResumesInPDFFormat(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("No file uploaded.");
        }
        try {
            String resumeData = readFromResumesServices.readFromUploadedResumeFile(file);
            logger.info("Writing Data in Pub/Sub Topic");
            pubSubPublisherService.messagePublisher(resumeData);
            return ResponseEntity.ok("Resume Data uploaded to pub/sub topic");
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Error processing PDF file: " + e.getMessage());
        }
    }
}