package com.noteiq.dto.document;

import com.noteiq.entity.Document;

import java.time.Instant;
import java.util.UUID;

public class DocumentResponse {

    private UUID id;
    private String title;
    private String subject;
    private String chapter;
    private String fileName;
    private Long fileSize;
    private Instant uploadedAt;

    public DocumentResponse() {
    }

    public DocumentResponse(UUID id, String title, String subject, String chapter, String fileName, Long fileSize, Instant uploadedAt) {
        this.id = id;
        this.title = title;
        this.subject = subject;
        this.chapter = chapter;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.uploadedAt = uploadedAt;
    }

    public static DocumentResponse fromEntity(Document document) {
        return new DocumentResponse(
                document.getId(),
                document.getTitle(),
                document.getSubject(),
                document.getChapter(),
                document.getFileName(),
                document.getFileSize(),
                document.getUploadedAt()
        );
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getChapter() {
        return chapter;
    }

    public void setChapter(String chapter) {
        this.chapter = chapter;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public Instant getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(Instant uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
}
