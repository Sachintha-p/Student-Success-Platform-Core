package com.sliit.studentplatform.module2.service.interfaces;

import org.springframework.web.multipart.MultipartFile;

public interface PdfExtractionService {
    /**
     * Takes an uploaded PDF file and extracts all readable text from it.
     */
    String extractTextFromPdf(MultipartFile file);
}