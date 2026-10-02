package com.noteiq.dto.quiz;

import com.noteiq.entity.Quiz;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class QuizResponse {

    private UUID id;
    private String title;
    private String difficulty;
    private Integer numberOfQuestions;
    private Instant createdAt;
    private UUID documentId;
    private String documentTitle;
    private List<QuestionResponse> questions;

    public QuizResponse() {
    }

    public QuizResponse(UUID id, String title, String difficulty, Integer numberOfQuestions, Instant createdAt, UUID documentId, String documentTitle, List<QuestionResponse> questions) {
        this.id = id;
        this.title = title;
        this.difficulty = difficulty;
        this.numberOfQuestions = numberOfQuestions;
        this.createdAt = createdAt;
        this.documentId = documentId;
        this.documentTitle = documentTitle;
        this.questions = questions;
    }

    public static QuizResponse forActiveQuiz(Quiz quiz) {
        List<QuestionResponse> questionResponses = quiz.getQuestions()
                .stream()
                .map(QuestionResponse::forActiveQuiz)
                .collect(Collectors.toList());

        return new QuizResponse(
                quiz.getId(),
                quiz.getTitle(),
                quiz.getDifficulty(),
                quiz.getNumberOfQuestions(),
                quiz.getCreatedAt(),
                quiz.getDocument().getId(),
                quiz.getDocument().getTitle(),
                questionResponses
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

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public Integer getNumberOfQuestions() {
        return numberOfQuestions;
    }

    public void setNumberOfQuestions(Integer numberOfQuestions) {
        this.numberOfQuestions = numberOfQuestions;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public UUID getDocumentId() {
        return documentId;
    }

    public void setDocumentId(UUID documentId) {
        this.documentId = documentId;
    }

    public String getDocumentTitle() {
        return documentTitle;
    }

    public void setDocumentTitle(String documentTitle) {
        this.documentTitle = documentTitle;
    }

    public List<QuestionResponse> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionResponse> questions) {
        this.questions = questions;
    }
}
