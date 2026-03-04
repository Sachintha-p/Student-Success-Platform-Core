package com.sliit.studentplatform.module2.service.impl;

import com.sliit.studentplatform.module2.service.interfaces.PdfExtractionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@Slf4j
public class PdfExtractionServiceImpl implements PdfExtractionService {

    @Override
    public String extractTextFromPdf(MultipartFile file) {
        log.info("Starting text extraction for uploaded file: {}", file.getOriginalFilename());

        try (PDDocument document = Loader.loadPDF(file.getBytes())) {
            PDFTextStripper stripper = new PDFTextStripper();
            String extractedText = stripper.getText(document);

            log.info("Successfully extracted {} characters from PDF.", extractedText.length());
            return extractedText;

        } catch (IOException e) {
            log.error("Failed to parse PDF file: {}", e.getMessage());
            throw new RuntimeException("Could not read the uploaded resume. Please ensure it is a valid PDF.", e);
        }
    }
}