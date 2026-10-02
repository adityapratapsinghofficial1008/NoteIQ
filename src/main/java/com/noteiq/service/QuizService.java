package com.noteiq.service;

import com.noteiq.dto.quiz.GenerateQuizRequest;
import com.noteiq.dto.quiz.QuizResponse;
import com.noteiq.entity.Document;
import com.noteiq.entity.Question;
import com.noteiq.entity.Quiz;
import com.noteiq.entity.User;
import com.noteiq.exception.BadRequestException;
import com.noteiq.exception.ResourceNotFoundException;
import com.noteiq.exception.UnauthorizedException;
import com.noteiq.repository.QuestionRepository;
import com.noteiq.repository.QuizRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class QuizService {

    private static final Set<Integer> ALLOWED_COUNTS = Set.of(5, 10, 15, 20);
    private static final Set<String> ALLOWED_DIFFICULTIES = Set.of("EASY", "MEDIUM", "HARD");

    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final DocumentService documentService;
    private final PdfService pdfService;
    private final UserService userService;
    private final QuizGenerationService quizGenerationService;

    public QuizService(QuizRepository quizRepository,
                       QuestionRepository questionRepository,
                       DocumentService documentService,
                       PdfService pdfService,
                       UserService userService,
                       QuizGenerationService quizGenerationService) {
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
        this.documentService = documentService;
        this.pdfService = pdfService;
        this.userService = userService;
        this.quizGenerationService = quizGenerationService;
    }

    @Transactional
    public QuizResponse generateQuiz(GenerateQuizRequest request) {
        validateQuizConfiguration(request.getNumberOfQuestions(), request.getDifficulty());

        User currentUser = userService.getCurrentAuthenticatedUser();
        Document document = documentService.getDocumentEntityForUser(request.getDocumentId(), currentUser.getId());

        // Extract text from the PDF document
        File pdfFile = new File(document.getFilePath());
        String extractedText = pdfService.extractText(pdfFile);

        // Build Quiz Entity
        String quizTitle = document.getTitle() + " - " + request.getDifficulty() + " Quiz";
        Quiz quiz = new Quiz(
                quizTitle,
                request.getDifficulty().toUpperCase(),
                request.getNumberOfQuestions(),
                document,
                currentUser
        );

        // Generate Questions via AI QuizGenerationService
        List<Question> generatedQuestions = quizGenerationService.generateQuestions(
                extractedText,
                request.getNumberOfQuestions(),
                request.getDifficulty().toUpperCase(),
                quiz
        );

        if (generatedQuestions == null || generatedQuestions.isEmpty()) {
            throw new BadRequestException("Failed to generate questions from document content");
        }

        quiz.setQuestions(generatedQuestions);

        // Save Quiz (Cascade will save Questions)
        Quiz savedQuiz = quizRepository.save(quiz);

        // Return Quiz Response WITHOUT exposing correct answers
        return QuizResponse.forActiveQuiz(savedQuiz);
    }

    public QuizResponse getQuizById(UUID quizId) {
        User currentUser = userService.getCurrentAuthenticatedUser();
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + quizId));

        if (!quiz.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You are not authorized to access this quiz");
        }

        return QuizResponse.forActiveQuiz(quiz);
    }

    private void validateQuizConfiguration(Integer count, String difficulty) {
        if (count == null || !ALLOWED_COUNTS.contains(count)) {
            throw new BadRequestException("Invalid question count. Allowed values: 5, 10, 15, 20");
        }
        if (difficulty == null || !ALLOWED_DIFFICULTIES.contains(difficulty.toUpperCase())) {
            throw new BadRequestException("Invalid difficulty. Allowed values: EASY, MEDIUM, HARD");
        }
    }
}
