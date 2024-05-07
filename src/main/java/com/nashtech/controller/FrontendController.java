package com.nashtech.controller;

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
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/resumes")
@AllArgsConstructor
public class FrontendController {
    final static Logger logger = LoggerFactory.getLogger(FrontendController.class);
    @Autowired
    private final ReadFromResumesServices readFromResumesServices;
    @Autowired
    private final SaveUploadedResumesService saveUploadedResumesService;

    @GetMapping("/report")
    public ResponseEntity<String> getTopCandidateProfile(){
        String url = "api";
        try{
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return ResponseEntity.ok(response.getBody());
        } catch (Exception exception) {
            return ResponseEntity.internalServerError().body("Error fetching the top candidates: " + exception.getMessage());
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadResumeData (@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("No file found / File not uploaded properly.");
        }
        try {
            RestTemplate restTemplate = new RestTemplate();
            String encodedParam = URLEncoder.encode(String.valueOf(file), StandardCharsets.UTF_8);
            String url = "http://192.168.1.116:5000/gpt-upload-resume?query=" + encodedParam;
            String resumeData = readFromResumesServices.readFromUploadedResumeFile(file);
            saveUploadedResumesService.saveResumes(file, resumeData);
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return ResponseEntity.ok(response.getBody());
        } catch (Exception exception) {
            return ResponseEntity.internalServerError().body("Error processing Resume: " + exception.getMessage());
        }
    }
}