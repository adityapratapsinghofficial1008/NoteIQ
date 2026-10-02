package com.noteiq.controller;

import com.noteiq.dto.quiz.GenerateQuizRequest;
import com.noteiq.dto.quiz.QuizResponse;
import com.noteiq.dto.quiz.QuizResultResponse;
import com.noteiq.dto.quiz.SubmitQuizRequest;
import com.noteiq.service.QuizEvaluationService;
import com.noteiq.service.QuizService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/quizzes")
public class QuizController {

    private final QuizService quizService;
    private final QuizEvaluationService quizEvaluationService;

    public QuizController(QuizService quizService, QuizEvaluationService quizEvaluationService) {
        this.quizService = quizService;
        this.quizEvaluationService = quizEvaluationService;
    }

    @PostMapping("/generate")
    public ResponseEntity<QuizResponse> generateQuiz(@Valid @RequestBody GenerateQuizRequest request) {
        QuizResponse response = quizService.generateQuiz(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuizResponse> getQuizById(@PathVariable UUID id) {
        QuizResponse response = quizService.getQuizById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<QuizResultResponse> submitQuiz(
            @PathVariable UUID id,
            @Valid @RequestBody SubmitQuizRequest request
    ) {
        QuizResultResponse response = quizEvaluationService.submitQuiz(id, request);
        return ResponseEntity.ok(response);
    }
}
