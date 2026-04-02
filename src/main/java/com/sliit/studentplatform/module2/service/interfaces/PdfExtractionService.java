package com.sliit.studentplatform.module2.service.interfaces;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface PdfExtractionService {
    // Notice the lowercase "df" at the end here
    String extractTextFromPdf(MultipartFile file) throws IOException;
}