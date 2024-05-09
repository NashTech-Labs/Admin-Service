package com.nashtech.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nashtech.config.LLMConfig;
import com.nashtech.entity.JobKeywords;
import com.nashtech.entity.ResumeRequest;
import com.nashtech.service.ReadFromResumesServices;
import com.nashtech.service.SaveUploadedResumesService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

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
    private final LLMConfig llmConfig;
    @Autowired
    private final RestTemplate restTemplate;

    @PostMapping("/fetch-resume")
    public ResponseEntity<JsonNode> fetchResume(@RequestBody JobKeywords keywords) {

        try {
            ResponseEntity<String> stringResponseEntity = restTemplate.postForEntity(llmConfig.getFetchResume(), keywords, String.class);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode candidateJsonResponse = objectMapper.readTree(stringResponseEntity.getBody());
            logger.info(candidateJsonResponse.toString());
            JsonNode filteredCandidateInformation = filterCandidateInformation(candidateJsonResponse.toPrettyString());

            String jsonString = filteredCandidateInformation.toString();
            JsonNode jsonNode = objectMapper.readTree(jsonString);

            return ResponseEntity.ok(jsonNode);


        } catch (Exception exception) {
            String errorMessage = "Error fetching Resumes: " + exception.getMessage();
            logger.error(errorMessage);

            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode errorResponse = objectMapper.createObjectNode();
            errorResponse.put("error", errorMessage);

            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<Object> uploadResumeData (@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("No file found / File not uploaded properly.");
        }
        try {
            String resumeData = readFromResumesServices.readFromUploadedResumeFile(file);
            HashMap<String, String> resumeInfo = saveUploadedResumesService.saveResumes(file);
            if (resumeInfo.containsKey("error")) {
                return ResponseEntity.badRequest().body(resumeInfo);
            }
            ResumeRequest request = new ResumeRequest();
            request.setPath(resumeInfo.get("path"));
            request.setCandidateID(resumeInfo.get("candidateID"));
            return ResponseEntity.ok(sendResumeRequest(request));
        } catch (Exception exception) {
            return ResponseEntity.internalServerError().body("Error processing Resume: " + exception.getMessage());
        }
    }

    private Object sendResumeRequest(ResumeRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<ResumeRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<Object> response = restTemplate.exchange(
                llmConfig.getAnalysisResumeUrl(),
                HttpMethod.POST,
                entity,
                Object.class);
        logger.info("Response from third-party API: " + response.getBody());
        return response.getBody();
    }

    private JsonNode filterCandidateInformation(String response) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(response);

        try (InputStream input = getClass().getResourceAsStream("/fields.properties")) {
            Properties properties = new Properties();
            properties.load(input);
            String[] fields = properties.getProperty("filter_fields").split(",");
            Set<String> fieldSet = new HashSet<>(Arrays.asList(fields));

            ArrayNode filteredCandidates = mapper.createArrayNode();

            if (jsonNode.isArray()) {
                for (JsonNode candidateNode : jsonNode) {
                    ObjectNode filteredCandidate = mapper.createObjectNode();

                    for (String field : fieldSet) {
                        field = field.trim();
                        if (candidateNode.has(field)) {
                            JsonNode fieldValue = candidateNode.get(field);
                            filteredCandidate.set(field, fieldValue);
                        }
                    }

                    filteredCandidates.add(filteredCandidate);
                }
            }

            return filteredCandidates;
        }
    }
}