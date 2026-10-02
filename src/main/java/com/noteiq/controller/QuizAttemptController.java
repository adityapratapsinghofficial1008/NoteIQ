package com.noteiq.controller;

import com.noteiq.dto.quiz.QuizResultResponse;
import com.noteiq.service.QuizEvaluationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/quiz-attempts")
public class QuizAttemptController {

    private final QuizEvaluationService quizEvaluationService;

    public QuizAttemptController(QuizEvaluationService quizEvaluationService) {
        this.quizEvaluationService = quizEvaluationService;
    }

    @GetMapping
    public ResponseEntity<List<QuizResultResponse>> getUserAttempts() {
        List<QuizResultResponse> response = quizEvaluationService.getUserAttempts();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuizResultResponse> getAttemptById(@PathVariable UUID id) {
        QuizResultResponse response = quizEvaluationService.getAttemptById(id);
        return ResponseEntity.ok(response);
    }
}
