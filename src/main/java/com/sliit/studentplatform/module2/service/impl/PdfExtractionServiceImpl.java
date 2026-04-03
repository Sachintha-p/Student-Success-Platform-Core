package com.sliit.studentplatform.module2.service.impl;

import com.sliit.studentplatform.module2.service.interfaces.PdfExtractionService;
import org.apache.pdfbox.Loader; // Import the new Loader class
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class PdfExtractionServiceImpl implements PdfExtractionService {

    @Override
    public String extractTextFromPdf(MultipartFile file) throws IOException {
        // Use Loader.loadPDF() instead of PDDocument.load() for PDFBox 3.x
        try (PDDocument document = Loader.loadPDF(file.getBytes())) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }
}