package com.noteiq.service;

import com.noteiq.dto.quiz.QuizResultResponse;
import com.noteiq.dto.quiz.SubmitQuizRequest;
import com.noteiq.entity.*;
import com.noteiq.exception.BadRequestException;
import com.noteiq.exception.ResourceNotFoundException;
import com.noteiq.exception.UnauthorizedException;
import com.noteiq.repository.QuizAttemptRepository;
import com.noteiq.repository.QuizRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class QuizEvaluationService {

    private final QuizRepository quizRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final UserService userService;

    public QuizEvaluationService(QuizRepository quizRepository,
                                 QuizAttemptRepository quizAttemptRepository,
                                 UserService userService) {
        this.quizRepository = quizRepository;
        this.quizAttemptRepository = quizAttemptRepository;
        this.userService = userService;
    }

    @Transactional
    public QuizResultResponse submitQuiz(UUID quizId, SubmitQuizRequest request) {
        User currentUser = userService.getCurrentAuthenticatedUser();
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + quizId));

        if (!quiz.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You are not authorized to submit answers for this quiz");
        }

        Map<UUID, Question> questionMap = quiz.getQuestions()
                .stream()
                .collect(Collectors.toMap(Question::getId, Function.identity()));

        int score = 0;
        int totalQuestions = quiz.getQuestions().size();
        List<QuizAnswer> quizAnswers = new ArrayList<>();
        List<QuizResultResponse.QuestionResult> resultList = new ArrayList<>();

        Map<UUID, String> submittedAnswersMap = new HashMap<>();
        if (request.getAnswers() != null) {
            for (SubmitQuizRequest.SingleAnswerRequest ans : request.getAnswers()) {
                submittedAnswersMap.put(ans.getQuestionId(), ans.getSelectedAnswer());
            }
        }

        QuizAttempt attempt = new QuizAttempt(0, totalQuestions, 0.0, quiz, currentUser);

        for (Question question : quiz.getQuestions()) {
            String studentSelected = submittedAnswersMap.getOrDefault(question.getId(), "");
            boolean isCorrect = question.getCorrectAnswer().equalsIgnoreCase(studentSelected.trim());

            if (isCorrect) {
                score++;
            }

            QuizAnswer quizAnswer = new QuizAnswer(
                    studentSelected,
                    isCorrect,
                    attempt,
                    question
            );
            quizAnswers.add(quizAnswer);

            QuizResultResponse.QuestionResult questionResult = new QuizResultResponse.QuestionResult(
                    question.getId(),
                    question.getQuestionText(),
                    question.getOptionA(),
                    question.getOptionB(),
                    question.getOptionC(),
                    question.getOptionD(),
                    studentSelected,
                    question.getCorrectAnswer(),
                    isCorrect,
                    question.getExplanation()
            );
            resultList.add(questionResult);
        }

        double percentage = totalQuestions > 0 ? ((double) score / totalQuestions) * 100.0 : 0.0;
        // Round to 2 decimal places
        percentage = Math.round(percentage * 100.0) / 100.0;

        attempt.setScore(score);
        attempt.setPercentage(percentage);
        attempt.setAnswers(quizAnswers);

        QuizAttempt savedAttempt = quizAttemptRepository.save(attempt);

        return new QuizResultResponse(
                savedAttempt.getId(),
                quiz.getId(),
                quiz.getTitle(),
                quiz.getDocument().getTitle(),
                savedAttempt.getScore(),
                savedAttempt.getTotalQuestions(),
                savedAttempt.getPercentage(),
                savedAttempt.getCompletedAt(),
                resultList
        );
    }

    public List<QuizResultResponse> getUserAttempts() {
        User currentUser = userService.getCurrentAuthenticatedUser();
        List<QuizAttempt> attempts = quizAttemptRepository.findByUserIdOrderByCompletedAtDesc(currentUser.getId());

        return attempts.stream().map(attempt -> {
            Quiz quiz = attempt.getQuiz();
            return new QuizResultResponse(
                    attempt.getId(),
                    quiz.getId(),
                    quiz.getTitle(),
                    quiz.getDocument().getTitle(),
                    attempt.getScore(),
                    attempt.getTotalQuestions(),
                    attempt.getPercentage(),
                    attempt.getCompletedAt(),
                    null // Summary list does not load individual question details
            );
        }).collect(Collectors.toList());
    }

    public QuizResultResponse getAttemptById(UUID attemptId) {
        User currentUser = userService.getCurrentAuthenticatedUser();
        QuizAttempt attempt = quizAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz attempt not found with id: " + attemptId));

        if (!attempt.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You are not authorized to view this attempt");
        }

        Quiz quiz = attempt.getQuiz();
        Map<UUID, QuizAnswer> answerMap = attempt.getAnswers()
                .stream()
                .collect(Collectors.toMap(ans -> ans.getQuestion().getId(), Function.identity()));

        List<QuizResultResponse.QuestionResult> resultList = quiz.getQuestions().stream().map(question -> {
            QuizAnswer answer = answerMap.get(question.getId());
            String studentSelected = answer != null ? answer.getSelectedAnswer() : "";
            boolean isCorrect = answer != null ? answer.getIsCorrect() : false;

            return new QuizResultResponse.QuestionResult(
                    question.getId(),
                    question.getQuestionText(),
                    question.getOptionA(),
                    question.getOptionB(),
                    question.getOptionC(),
                    question.getOptionD(),
                    studentSelected,
                    question.getCorrectAnswer(),
                    isCorrect,
                    question.getExplanation()
            );
        }).collect(Collectors.toList());

        return new QuizResultResponse(
                attempt.getId(),
                quiz.getId(),
                quiz.getTitle(),
                quiz.getDocument().getTitle(),
                attempt.getScore(),
                attempt.getTotalQuestions(),
                attempt.getPercentage(),
                attempt.getCompletedAt(),
                resultList
        );
    }
}
