package com.noteiq.dto.quiz;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public class SubmitQuizRequest {

    @NotEmpty(message = "Answers list cannot be empty")
    @Valid
    private List<SingleAnswerRequest> answers;

    public SubmitQuizRequest() {
    }

    public SubmitQuizRequest(List<SingleAnswerRequest> answers) {
        this.answers = answers;
    }

    public List<SingleAnswerRequest> getAnswers() {
        return answers;
    }

    public void setAnswers(List<SingleAnswerRequest> answers) {
        this.answers = answers;
    }

    public static class SingleAnswerRequest {

        @NotNull(message = "Question ID is required")
        private UUID questionId;

        @NotBlank(message = "Selected answer is required")
        private String selectedAnswer;

        public SingleAnswerRequest() {
        }

        public SingleAnswerRequest(UUID questionId, String selectedAnswer) {
            this.questionId = questionId;
            this.selectedAnswer = selectedAnswer;
        }

        public UUID getQuestionId() {
            return questionId;
        }

        public void setQuestionId(UUID questionId) {
            this.questionId = questionId;
        }

        public String getSelectedAnswer() {
            return selectedAnswer;
        }

        public void setSelectedAnswer(String selectedAnswer) {
            this.selectedAnswer = selectedAnswer;
        }
    }
}
