package com.noteiq.service;

import com.noteiq.exception.BadRequestException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
public class PdfService {

    public void validatePdf(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Uploaded file is empty");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.toLowerCase().endsWith(".pdf")) {
            throw new BadRequestException("Only PDF files are allowed");
        }

        String contentType = file.getContentType();
        if (contentType != null && !contentType.equals("application/pdf") && !contentType.equals("application/x-pdf")) {
            throw new BadRequestException("File content type must be application/pdf");
        }
    }

    public String extractText(File pdfFile) {
        if (!pdfFile.exists() || !pdfFile.canRead()) {
            throw new BadRequestException("PDF file does not exist or is unreadable");
        }

        try (PDDocument document = Loader.loadPDF(pdfFile)) {
            if (document.isEncrypted()) {
                throw new BadRequestException("Encrypted PDF files are not supported");
            }

            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);

            if (text == null || text.trim().isEmpty()) {
                throw new BadRequestException("Extracted text from PDF is empty or unreadable");
            }

            return text.trim();
        } catch (IOException e) {
            throw new BadRequestException("Failed to process or read PDF document: " + e.getMessage());
        }
    }
}
