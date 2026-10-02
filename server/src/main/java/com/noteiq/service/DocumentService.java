package com.noteiq.service;

import com.noteiq.dto.document.DocumentResponse;
import com.noteiq.entity.Document;
import com.noteiq.entity.User;
import com.noteiq.exception.BadRequestException;
import com.noteiq.exception.ResourceNotFoundException;
import com.noteiq.exception.UnauthorizedException;
import com.noteiq.repository.DocumentRepository;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DocumentService {

    @Value("${app.upload.dir:./uploads}")
    private String uploadDir;

    private final DocumentRepository documentRepository;
    private final UserService userService;
    private final PdfService pdfService;

    public DocumentService(DocumentRepository documentRepository, UserService userService, PdfService pdfService) {
        this.documentRepository = documentRepository;
        this.userService = userService;
        this.pdfService = pdfService;
    }

    @PostConstruct
    public void init() {
        try {
            Path path = Paths.get(uploadDir);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage directory: " + uploadDir, e);
        }
    }

    public DocumentResponse uploadDocument(MultipartFile file, String title, String subject, String chapter) {
        pdfService.validatePdf(file);

        User currentUser = userService.getCurrentAuthenticatedUser();

        String originalFilename = file.getOriginalFilename();
        String safeStoredFileName = UUID.randomUUID().toString() + "_" + (originalFilename != null ? originalFilename.replaceAll("[^a-zA-Z0-9.-]", "_") : "document.pdf");
        Path targetPath = Paths.get(uploadDir).resolve(safeStoredFileName);

        try {
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new BadRequestException("Could not store file: " + e.getMessage());
        }

        Document document = new Document(
                title != null && !title.isBlank() ? title : originalFilename,
                subject,
                chapter,
                safeStoredFileName,
                targetPath.toAbsolutePath().toString(),
                file.getSize(),
                currentUser
        );

        Document saved = documentRepository.save(document);
        return DocumentResponse.fromEntity(saved);
    }

    public List<DocumentResponse> getUserDocuments() {
        User currentUser = userService.getCurrentAuthenticatedUser();
        return documentRepository.findByUserId(currentUser.getId())
                .stream()
                .map(DocumentResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public DocumentResponse getDocumentById(UUID id) {
        User currentUser = userService.getCurrentAuthenticatedUser();
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with id: " + id));

        if (!document.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You are not authorized to view this document");
        }

        return DocumentResponse.fromEntity(document);
    }

    public Document getDocumentEntityForUser(UUID id, UUID userId) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with id: " + id));

        if (!document.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to access this document");
        }

        return document;
    }

    public void deleteDocument(UUID id) {
        User currentUser = userService.getCurrentAuthenticatedUser();
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with id: " + id));

        if (!document.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You are not authorized to delete this document");
        }

        try {
            Path filePath = Paths.get(document.getFilePath());
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            // Log file deletion failure but continue to delete entity record
        }

        documentRepository.delete(document);
    }
}
