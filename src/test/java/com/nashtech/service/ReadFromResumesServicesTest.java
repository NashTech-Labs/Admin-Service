package com.nashtech.service;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

@ContextConfiguration(classes = {ReadFromResumesServices.class})
@ExtendWith(SpringExtension.class)
class ReadFromResumesServicesTest {
    @Autowired
    private ReadFromResumesServices readFromResumesServices;

    /**
     * Method under test:
     * {@link ReadFromResumesServices#readFromUploadedResumeFile(MultipartFile)}
     */
    @Test
    void testReadFromUploadedResumeFile() throws IOException {
        // Arrange, Act and Assert
        assertThrows(IOException.class, () -> readFromResumesServices.readFromUploadedResumeFile(
                new MockMultipartFile("Name", new ByteArrayInputStream("AXAXAXAX".getBytes("UTF-8")))));
        assertThrows(IOException.class,
                () -> readFromResumesServices
                        .readFromUploadedResumeFile(new MockMultipartFile("Started Reading resume Data from uploaded Resume",
                                new ByteArrayInputStream("AXAXAXAX".getBytes("UTF-8")))));
    }
}
