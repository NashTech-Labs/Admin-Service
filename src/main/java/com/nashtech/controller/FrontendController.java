package com.nashtech.controller;

import com.nashtech.service.PubSubPublisherService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

@RestController
@RequestMapping("/resumes")
public class FrontendController {
    final static Logger logger = LoggerFactory.getLogger(FrontendController.class);
    private final Path fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();
    private final PubSubPublisherService pubSubPublisherService;

    public FrontendController(PubSubPublisherService pubSubPublisherService) {
        this.pubSubPublisherService = pubSubPublisherService;
    }

    @GetMapping("/get")
    public String getMessage(){
        return "Hello";
    }

    @PostMapping("/upload")
    public ResponseEntity<String> getResumes(@RequestParam("file") MultipartFile file) {
        try {
            Files.createDirectories(this.fileStorageLocation);
            Path targetLocation = this.fileStorageLocation.resolve(Objects.requireNonNull(file.getOriginalFilename()));
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            byte[] fileContent = file.getBytes();
            String messageContent = new String(fileContent, StandardCharsets.UTF_8);
            System.out.println(messageContent);
            pubSubPublisherService.messagePublisher(messageContent);
            return ResponseEntity.ok("File uploaded successfully: " + file.getOriginalFilename());
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Could not store file " + file.getOriginalFilename() + ". Please try again!");
        }
    }

    @PostMapping("/pdf")
    public ResponseEntity<String> getResumesInPDFFormat(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("No file uploaded.");
        }

        try (InputStream pdfInputStream = file.getInputStream()) {
            // Load PDF document from input stream
            logger.info("Started Reading Text from uploaded PDF\n");
            PDDocument document = PDDocument.load(pdfInputStream);
            String extractedText = extractTextFromPdf(document);
            document.close();
            logger.info("Extracted Text from PDF:\n" + extractedText);
            return ResponseEntity.ok("File uploaded and text extracted. Check console for output.");
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Error processing PDF file: " + e.getMessage());
        }
    }

    private String extractTextFromPdf(PDDocument document) throws IOException {
        PDFTextStripper stripper = new PDFTextStripper();
        return stripper.getText(document).replaceAll("[^a-zA-Z0-9\\s]", "");
    }
}