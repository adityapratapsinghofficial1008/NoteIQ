package com.noteiq;

import com.noteiq.dto.quiz.QuizResultResponse;
import com.noteiq.dto.quiz.SubmitQuizRequest;
import com.noteiq.entity.*;
import com.noteiq.repository.QuizAttemptRepository;
import com.noteiq.repository.QuizRepository;
import com.noteiq.service.QuizEvaluationService;
import com.noteiq.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuizEvaluationServiceTest {

    @Mock
    private QuizRepository quizRepository;

    @Mock
    private QuizAttemptRepository quizAttemptRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private QuizEvaluationService quizEvaluationService;

    private User user;
    private Document document;
    private Quiz quiz;
    private Question question1;
    private Question question2;
    private UUID quizId;

    @BeforeEach
    void setUp() {
        UUID userId = UUID.randomUUID();
        user = new User("John Doe", "john@example.com", "password");
        user.setId(userId);

        document = new Document("Math Notes", "Math", "1", "file.pdf", "/path/file.pdf", 100L, user);

        quizId = UUID.randomUUID();
        quiz = new Quiz("Math Quiz", "EASY", 2, document, user);
        quiz.setId(quizId);

        question1 = new Question("What is 2+2?", "3", "4", "5", "6", "B", "2+2=4", quiz);
        question1.setId(UUID.randomUUID());

        question2 = new Question("What is 3*3?", "6", "9", "12", "15", "B", "3*3=9", quiz);
        question2.setId(UUID.randomUUID());

        quiz.setQuestions(List.of(question1, question2));
    }

    @Test
    void submitQuiz_EvaluatesScoreAndPercentageCorrectly() {
        when(userService.getCurrentAuthenticatedUser()).thenReturn(user);
        when(quizRepository.findById(quizId)).thenReturn(Optional.of(quiz));
        when(quizAttemptRepository.save(any(QuizAttempt.class))).thenAnswer(invocation -> {
            QuizAttempt attempt = invocation.getArgument(0);
            attempt.setId(UUID.randomUUID());
            return attempt;
        });

        SubmitQuizRequest request = new SubmitQuizRequest(List.of(
                new SubmitQuizRequest.SingleAnswerRequest(question1.getId(), "B"), // Correct
                new SubmitQuizRequest.SingleAnswerRequest(question2.getId(), "A")  // Incorrect
        ));

        QuizResultResponse result = quizEvaluationService.submitQuiz(quizId, request);

        assertNotNull(result);
        assertEquals(1, result.getScore());
        assertEquals(2, result.getTotalQuestions());
        assertEquals(50.0, result.getPercentage());
        assertEquals(2, result.getResults().size());
        assertTrue(result.getResults().stream().anyMatch(r -> r.getQuestionId().equals(question1.getId()) && r.getIsCorrect()));
        assertFalse(result.getResults().stream().anyMatch(r -> r.getQuestionId().equals(question2.getId()) && r.getIsCorrect()));
    }
}
