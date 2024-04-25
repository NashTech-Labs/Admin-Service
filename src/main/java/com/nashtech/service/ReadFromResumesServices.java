package com.nashtech.service;

import lombok.AllArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
@AllArgsConstructor
public class ReadFromResumesServices {
    private final static Logger logger = LoggerFactory.getLogger(ReadFromResumesServices.class);

    public String readFromUploadedResumeFile(MultipartFile file) throws IOException {
        try (InputStream pdfInputStream = file.getInputStream()) {
            // Load PDF document from input stream
            logger.info("Started Reading resume Data from uploaded Resume");
            PDDocument document = PDDocument.load(pdfInputStream);
            String extractedText = extractTextFromPdf(document);
            document.close();
            logger.info("Extracted Text from PDF:\n" + extractedText);
            return extractedText;
        } catch (IOException e) {
            throw new IOException(e);
        }
    }

    private String extractTextFromPdf(PDDocument document) throws IOException {
        PDFTextStripper stripper = new PDFTextStripper();
        return stripper.getText(document).replaceAll("[^a-zA-Z0-9\\s]", "");
    }
}
