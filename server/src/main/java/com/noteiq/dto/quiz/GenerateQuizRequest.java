package com.noteiq.dto.quiz;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class GenerateQuizRequest {

    @NotNull(message = "Document ID is required")
    private UUID documentId;

    @NotNull(message = "Number of questions is required")
    @Min(value = 5, message = "Minimum number of questions is 5")
    @Max(value = 20, message = "Maximum number of questions is 20")
    private Integer numberOfQuestions;

    @NotBlank(message = "Difficulty is required")
    private String difficulty;

    public GenerateQuizRequest() {
    }

    public GenerateQuizRequest(UUID documentId, Integer numberOfQuestions, String difficulty) {
        this.documentId = documentId;
        this.numberOfQuestions = numberOfQuestions;
        this.difficulty = difficulty;
    }

    public UUID getDocumentId() {
        return documentId;
    }

    public void setDocumentId(UUID documentId) {
        this.documentId = documentId;
    }

    public Integer getNumberOfQuestions() {
        return numberOfQuestions;
    }

    public void setNumberOfQuestions(Integer numberOfQuestions) {
        this.numberOfQuestions = numberOfQuestions;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }
}
