package com.nashtech.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nashtech.service.ReadFromResumesServices;
import com.nashtech.service.SaveUploadedResumesService;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

@ContextConfiguration(classes = {FrontendController.class})
@ExtendWith(SpringExtension.class)
class FrontendControllerTest {
    @Autowired
    private FrontendController frontendController;

    @MockBean
    private ReadFromResumesServices readFromResumesServices;

    @MockBean
    private SaveUploadedResumesService saveUploadedResumesService;

    @Test
    void testGetResumesDataAndAnalysisReport() throws IOException {

        // Arrange
        ReadFromResumesServices readFromResumesServices = new ReadFromResumesServices();
        FrontendController frontendController = new FrontendController(readFromResumesServices,
                new SaveUploadedResumesService());

        // Act
        ResponseEntity<String> actualResumesDataAndAnalysisReport = frontendController.uploadResumeData(
                new MockMultipartFile("Name", new ByteArrayInputStream("AXAXAXAX".getBytes("UTF-8"))));

        // Assert
        assertEquals("Error processing Resume: java.io.IOException: Error: End-of-File, expected line",
                actualResumesDataAndAnalysisReport.getBody());
        assertEquals(500, actualResumesDataAndAnalysisReport.getStatusCodeValue());
        assertTrue(actualResumesDataAndAnalysisReport.getHeaders().isEmpty());
    }

    @Test
    void testGetResumesDataAndAnalysisReport2() throws IOException {

        // Arrange
        FrontendController frontendController = new FrontendController(null, new SaveUploadedResumesService());

        // Act
        ResponseEntity<String> actualResumesDataAndAnalysisReport = frontendController.uploadResumeData(
                new MockMultipartFile("Name", new ByteArrayInputStream("AXAXAXAX".getBytes("UTF-8"))));

        // Assert
        assertEquals("Error processing Resume: Cannot invoke \"com.nashtech.service.ReadFromResumesServices.readFromUpload"
                + "edResumeFile(org.springframework.web.multipart.MultipartFile)\" because \"this.readFromResumesServices\""
                + " is null", actualResumesDataAndAnalysisReport.getBody());
        assertEquals(500, actualResumesDataAndAnalysisReport.getStatusCodeValue());
        assertTrue(actualResumesDataAndAnalysisReport.getHeaders().isEmpty());
    }

    @Test
    void testGetResumesDataAndAnalysisReport4() throws IOException {

        // Arrange
        ReadFromResumesServices readFromResumesServices = mock(ReadFromResumesServices.class);
        when(readFromResumesServices.readFromUploadedResumeFile(Mockito.<MultipartFile>any()))
                .thenReturn("jane.doe@example.org");
        FrontendController frontendController = new FrontendController(readFromResumesServices, null);

        // Act
        ResponseEntity<String> actualResumesDataAndAnalysisReport = frontendController.uploadResumeData(
                new MockMultipartFile("Name", new ByteArrayInputStream("AXAXAXAX".getBytes("UTF-8"))));

        // Assert
        verify(readFromResumesServices).readFromUploadedResumeFile(Mockito.<MultipartFile>any());
        assertEquals("Error processing Resume: Cannot invoke \"com.nashtech.service.SaveUploadedResumesService.saveResumes"
                + "(org.springframework.web.multipart.MultipartFile, String)\" because \"this.saveUploadedResumesService\""
                + " is null", actualResumesDataAndAnalysisReport.getBody());
        assertEquals(500, actualResumesDataAndAnalysisReport.getStatusCodeValue());
        assertTrue(actualResumesDataAndAnalysisReport.getHeaders().isEmpty());
    }

    @Test
    void testGetResumesDataAndAnalysisReport6() throws IOException {

        // Arrange
        FrontendController frontendController = new FrontendController(mock(ReadFromResumesServices.class),
                mock(SaveUploadedResumesService.class));

        // Act
        ResponseEntity<String> actualResumesDataAndAnalysisReport = frontendController
                .uploadResumeData(new MockMultipartFile("Name", new ByteArrayInputStream(new byte[]{})));

        // Assert
        assertEquals("No file found / File not uploaded properly.", actualResumesDataAndAnalysisReport.getBody());
        assertEquals(400, actualResumesDataAndAnalysisReport.getStatusCodeValue());
        assertTrue(actualResumesDataAndAnalysisReport.getHeaders().isEmpty());
    }

}
