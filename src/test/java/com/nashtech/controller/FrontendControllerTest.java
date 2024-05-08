package com.nashtech.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nashtech.config.LLMConfig;
import com.nashtech.service.ReadFromResumesServices;
import com.nashtech.service.SaveUploadedResumesService;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

class FrontendControllerTest {
    /**
     * Method under test: {@link FrontendController#getTopCandidateProfile()}
     */
    @Test
    void testGetTopCandidateProfile() {


        // Arrange
        LLMConfig llmConfig = new LLMConfig();
        ReadFromResumesServices readFromResumesServices = new ReadFromResumesServices();
        FrontendController frontendController = new FrontendController(llmConfig, readFromResumesServices,
                new SaveUploadedResumesService());

        // Act
        ResponseEntity<String> actualTopCandidateProfile = frontendController.getTopCandidateProfile();

        // Assert
        assertEquals("Error fetching the top candidates: URI is not absolute", actualTopCandidateProfile.getBody());
        assertEquals(500, actualTopCandidateProfile.getStatusCodeValue());
        assertTrue(actualTopCandidateProfile.getHeaders().isEmpty());
    }

    /**
     * Method under test: {@link FrontendController#getTopCandidateProfile()}
     */
    @Test
    void testGetTopCandidateProfile2() {

        // Arrange
        ReadFromResumesServices readFromResumesServices = new ReadFromResumesServices();
        FrontendController frontendController = new FrontendController(null, readFromResumesServices,
                new SaveUploadedResumesService());

        // Act
        ResponseEntity<String> actualTopCandidateProfile = frontendController.getTopCandidateProfile();

        // Assert
        assertEquals(500, actualTopCandidateProfile.getStatusCodeValue());
        assertTrue(actualTopCandidateProfile.getHeaders().isEmpty());
        String expectedBody = String.join("", "Error fetching the top candidates: Cannot invoke \"com.",
                System.getProperty("user.name"),
                ".config.LLMConfig.getTopCandidatesUrl()\" because \"this.llmConfig\" is null");
        assertEquals(expectedBody, actualTopCandidateProfile.getBody());
    }

    /**
     * Method under test: {@link FrontendController#getTopCandidateProfile()}
     */
    @Test
    void testGetTopCandidateProfile3() {


        // Arrange
        LLMConfig llmConfig = new LLMConfig();
        ReadFromResumesServices readFromResumesServices = mock(ReadFromResumesServices.class);
        FrontendController frontendController = new FrontendController(llmConfig, readFromResumesServices,
                new SaveUploadedResumesService());

        // Act
        ResponseEntity<String> actualTopCandidateProfile = frontendController.getTopCandidateProfile();

        // Assert
        assertEquals("Error fetching the top candidates: URI is not absolute", actualTopCandidateProfile.getBody());
        assertEquals(500, actualTopCandidateProfile.getStatusCodeValue());
        assertTrue(actualTopCandidateProfile.getHeaders().isEmpty());
    }


    /**
     * Method under test: {@link FrontendController#uploadResumeData(MultipartFile)}
     */
    @Test
    void testUploadResumeData() throws IOException {


        // Arrange
        LLMConfig llmConfig = new LLMConfig();
        ReadFromResumesServices readFromResumesServices = new ReadFromResumesServices();
        FrontendController frontendController = new FrontendController(llmConfig, readFromResumesServices,
                new SaveUploadedResumesService());
        MockMultipartFile file = new MockMultipartFile("Name", new ByteArrayInputStream("AXAXAXAX".getBytes("UTF-8")));

        // Act
        ResponseEntity<String> actualUploadResumeDataResult = frontendController.uploadResumeData(file);

        // Assert
        assertEquals("Error processing Resume: java.io.IOException: Error: End-of-File, expected line",
                actualUploadResumeDataResult.getBody());
        assertEquals(500, actualUploadResumeDataResult.getStatusCodeValue());
        assertTrue(actualUploadResumeDataResult.getHeaders().isEmpty());
    }

    /**
     * Method under test: {@link FrontendController#uploadResumeData(MultipartFile)}
     */
    @Test
    void testUploadResumeData2() throws IOException {

        // Arrange
        ReadFromResumesServices readFromResumesServices = new ReadFromResumesServices();
        FrontendController frontendController = new FrontendController(null, readFromResumesServices,
                new SaveUploadedResumesService());
        MockMultipartFile file = new MockMultipartFile("Name", new ByteArrayInputStream("AXAXAXAX".getBytes("UTF-8")));

        // Act
        ResponseEntity<String> actualUploadResumeDataResult = frontendController.uploadResumeData(file);

        // Assert
        assertEquals(500, actualUploadResumeDataResult.getStatusCodeValue());
        assertTrue(actualUploadResumeDataResult.getHeaders().isEmpty());
        String expectedBody = String.join("", "Error processing Resume: Cannot invoke \"com.",
                System.getProperty("user.name"),
                ".config.LLMConfig.getResumeStructureUrl()\" because \"this.llmConfig\" is null");
        assertEquals(expectedBody, actualUploadResumeDataResult.getBody());
    }

    /**
     * Method under test: {@link FrontendController#uploadResumeData(MultipartFile)}
     */
    @Test
    void testUploadResumeData3() throws IOException {

        // Arrange
        LLMConfig llmConfig = new LLMConfig();
        FrontendController frontendController = new FrontendController(llmConfig, null, new SaveUploadedResumesService());
        MockMultipartFile file = new MockMultipartFile("Name", new ByteArrayInputStream("AXAXAXAX".getBytes("UTF-8")));

        // Act
        ResponseEntity<String> actualUploadResumeDataResult = frontendController.uploadResumeData(file);

        // Assert
        assertEquals(500, actualUploadResumeDataResult.getStatusCodeValue());
        assertTrue(actualUploadResumeDataResult.getHeaders().isEmpty());
        String expectedBody = String.join("", "Error processing Resume: Cannot invoke \"com.",
                System.getProperty("user.name"),
                ".service.ReadFromResumesServices.readFromUploadedResumeFile(org.springframework.web.multipart.MultipartFile)\""
                        + " because \"this.readFromResumesServices\" is null");
        assertEquals(expectedBody, actualUploadResumeDataResult.getBody());
    }

    /**
     * Method under test: {@link FrontendController#uploadResumeData(MultipartFile)}
     */
    @Test
    void testUploadResumeData4() throws IOException {

        // Arrange
        ReadFromResumesServices readFromResumesServices = mock(ReadFromResumesServices.class);
        when(readFromResumesServices.readFromUploadedResumeFile(Mockito.<MultipartFile>any()))
                .thenReturn("jane.doe@example.org");
        LLMConfig llmConfig = new LLMConfig();
        FrontendController frontendController = new FrontendController(llmConfig, readFromResumesServices,
                new SaveUploadedResumesService());
        MockMultipartFile file = new MockMultipartFile("Name", new ByteArrayInputStream("AXAXAXAX".getBytes("UTF-8")));

        // Act
        ResponseEntity<String> actualUploadResumeDataResult = frontendController.uploadResumeData(file);

        // Assert
        verify(readFromResumesServices).readFromUploadedResumeFile(isA(MultipartFile.class));
        assertEquals("Error processing Resume: URI is not absolute", actualUploadResumeDataResult.getBody());
        assertEquals(500, actualUploadResumeDataResult.getStatusCodeValue());
        assertTrue(actualUploadResumeDataResult.getHeaders().isEmpty());
    }

    /**
     * Method under test: {@link FrontendController#uploadResumeData(MultipartFile)}
     */
    @Test
    void testUploadResumeData5() throws IOException {

        // Arrange
        ReadFromResumesServices readFromResumesServices = mock(ReadFromResumesServices.class);
        when(readFromResumesServices.readFromUploadedResumeFile(Mockito.<MultipartFile>any()))
                .thenReturn("jane.doe@example.org");
        FrontendController frontendController = new FrontendController(new LLMConfig(), readFromResumesServices, null);
        MockMultipartFile file = new MockMultipartFile("Name", new ByteArrayInputStream("AXAXAXAX".getBytes("UTF-8")));

        // Act
        ResponseEntity<String> actualUploadResumeDataResult = frontendController.uploadResumeData(file);

        // Assert
        verify(readFromResumesServices).readFromUploadedResumeFile(isA(MultipartFile.class));
        assertEquals(500, actualUploadResumeDataResult.getStatusCodeValue());
        assertTrue(actualUploadResumeDataResult.getHeaders().isEmpty());
        String expectedBody = String.join("", "Error processing Resume: Cannot invoke \"com.",
                System.getProperty("user.name"),
                ".service.SaveUploadedResumesService.saveResumes(org.springframework.web.multipart.MultipartFile,"
                        + " String)\" because \"this.saveUploadedResumesService\" is null");
        assertEquals(expectedBody, actualUploadResumeDataResult.getBody());
    }

    /**
     * Method under test: {@link FrontendController#uploadResumeData(MultipartFile)}
     */
    @Test
    void testUploadResumeData6() throws IOException {

        // Arrange
        ReadFromResumesServices readFromResumesServices = mock(ReadFromResumesServices.class);
        when(readFromResumesServices.readFromUploadedResumeFile(Mockito.<MultipartFile>any()))
                .thenReturn("jane.doe@example.org");
        SaveUploadedResumesService saveUploadedResumesService = mock(SaveUploadedResumesService.class);
        doNothing().when(saveUploadedResumesService).saveResumes(Mockito.<MultipartFile>any(), Mockito.<String>any());
        FrontendController frontendController = new FrontendController(new LLMConfig(), readFromResumesServices,
                saveUploadedResumesService);
        MockMultipartFile file = new MockMultipartFile("Name", new ByteArrayInputStream("AXAXAXAX".getBytes("UTF-8")));

        // Act
        ResponseEntity<String> actualUploadResumeDataResult = frontendController.uploadResumeData(file);

        // Assert
        verify(readFromResumesServices).readFromUploadedResumeFile(isA(MultipartFile.class));
        verify(saveUploadedResumesService).saveResumes(isA(MultipartFile.class), eq("jane.doe@example.org"));
        assertEquals("Error processing Resume: URI is not absolute", actualUploadResumeDataResult.getBody());
        assertEquals(500, actualUploadResumeDataResult.getStatusCodeValue());
        assertTrue(actualUploadResumeDataResult.getHeaders().isEmpty());
    }

    /**
     * Method under test: {@link FrontendController#uploadResumeData(MultipartFile)}
     */
    @Test
    void testUploadResumeData7() throws IOException {

        // Arrange
        FrontendController frontendController = new FrontendController(new LLMConfig(), mock(ReadFromResumesServices.class),
                mock(SaveUploadedResumesService.class));
        MockMultipartFile file = new MockMultipartFile("Name", new ByteArrayInputStream(new byte[]{}));

        // Act
        ResponseEntity<String> actualUploadResumeDataResult = frontendController.uploadResumeData(file);

        // Assert
        assertEquals("No file found / File not uploaded properly.", actualUploadResumeDataResult.getBody());
        assertEquals(400, actualUploadResumeDataResult.getStatusCodeValue());
        assertTrue(actualUploadResumeDataResult.getHeaders().isEmpty());
    }

}
