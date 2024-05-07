package com.nashtech.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nashtech.config.GCPConfig;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

@ContextConfiguration(classes = {SaveUploadedResumesService.class, GCPConfig.class})
@ExtendWith(SpringExtension.class)
class SaveUploadedResumesServiceTest {
    @Autowired
    private GCPConfig gCPConfig;

    @MockBean
    private ObjectMapper objectMapper;

    @Autowired
    private SaveUploadedResumesService saveUploadedResumesService;

    /**
     * Method under test:
     * {@link SaveUploadedResumesService#saveResumes(MultipartFile, String)}
     */
    @Test
    void testSaveResumes() throws IOException {
        // Arrange
        when(objectMapper.writeValueAsString(Mockito.<Object>any())).thenReturn("42");

        // Act
        saveUploadedResumesService.saveResumes(
                new MockMultipartFile("Name", new ByteArrayInputStream("AXAXAXAX".getBytes("UTF-8"))), "Resume Data");

        // Assert
        verify(objectMapper).writeValueAsString(Mockito.<Object>any());
    }

    /**
     * Method under test:
     * {@link SaveUploadedResumesService#saveResumes(MultipartFile, String)}
     */
    @Test
    void testSaveResumes2() throws IOException {
        // Arrange
        when(objectMapper.writeValueAsString(Mockito.<Object>any())).thenReturn("https://");

        // Act
        saveUploadedResumesService.saveResumes(
                new MockMultipartFile("Name", new ByteArrayInputStream("AXAXAXAX".getBytes("UTF-8"))), "Resume Data");

        // Assert
        verify(objectMapper).writeValueAsString(Mockito.<Object>any());
    }

    /**
     * Method under test:
     * {@link SaveUploadedResumesService#saveResumes(MultipartFile, String)}
     */
    @Test
    void testSaveResumes3() throws IOException {
        // Arrange
        when(objectMapper.writeValueAsString(Mockito.<Object>any())).thenReturn("");

        // Act
        saveUploadedResumesService.saveResumes(
                new MockMultipartFile("Name", new ByteArrayInputStream("AXAXAXAX".getBytes("UTF-8"))), "Resume Data");

        // Assert
        verify(objectMapper).writeValueAsString(Mockito.<Object>any());
    }

    /**
     * Method under test:
     * {@link SaveUploadedResumesService#saveResumes(MultipartFile, String)}
     */
    @Test
    void testSaveResumes4() throws JsonProcessingException {
        // Arrange
        when(objectMapper.writeValueAsString(Mockito.<Object>any())).thenReturn("42");
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("foo.txt");

        // Act
        saveUploadedResumesService.saveResumes(file, "");

        // Assert
        verify(objectMapper).writeValueAsString(Mockito.<Object>any());
        verify(file).getOriginalFilename();
    }
}
