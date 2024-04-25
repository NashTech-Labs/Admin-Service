package com.nashtech.service;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

@Service
@AllArgsConstructor
public class SaveUploadedResumesService {
    private final static Logger logger = LoggerFactory.getLogger(SaveUploadedResumesService.class);
    private final Path fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();

    public boolean saveResumes(MultipartFile file) {
        try {
            Files.createDirectories(this.fileStorageLocation);
            Path targetLocation = this.fileStorageLocation.resolve(Objects.requireNonNull(file.getOriginalFilename()));
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            logger.info("File Stored Successfully");
            return true;
        } catch (IOException e) {
            logger.info("Error in Storing the File : {}", e.getMessage());
            return false;
        }
    }

}
