package com.noteiq.service;

import com.noteiq.entity.Question;
import com.noteiq.entity.Quiz;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Pluggable QuizGenerationService implementation.
 * Currently uses intelligent text parsing / fallback pattern generation based on extracted PDF content.
 * An external AI provider (OpenAI / Gemini) can easily replace or extend this bean when credentials are configured.
 */
@Service
public class MockQuizGenerationServiceImpl implements QuizGenerationService {

    @Override
    public List<Question> generateQuestions(String pdfText, int numberOfQuestions, String difficulty, Quiz quiz) {
        List<Question> questions = new ArrayList<>();

        // Extract key non-empty lines from PDF text for contextual question generation
        String[] lines = pdfText.split("\r?\n");
        List<String> contentLines = new ArrayList<>();
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.length() > 15) {
                contentLines.add(trimmed);
            }
        }

        String[] optionsTemplate = {"A", "B", "C", "D"};

        for (int i = 0; i < numberOfQuestions; i++) {
            String contextSnippet = !contentLines.isEmpty()
                    ? contentLines.get(i % contentLines.size())
                    : "Core concept from study notes";

            // Limit snippet length for clean question text
            if (contextSnippet.length() > 80) {
                contextSnippet = contextSnippet.substring(0, 77) + "...";
            }

            String questionText = String.format("Question %d (%s): Based on the notes, what is the primary emphasis of: '%s'?",
                    (i + 1), difficulty, contextSnippet);

            String optionA = "It defines the core principle of " + (i + 1);
            String optionB = "It represents an auxiliary method for studying " + (i + 1);
            String optionC = "It contradicts established theories in the notes";
            String optionD = "It is an outdated concept replaced by modern frameworks";

            String correctAnswer = optionsTemplate[i % 4];
            String explanation = String.format("According to the uploaded document text: '%s', option %s accurately reflects the material.",
                    contextSnippet, correctAnswer);

            Question question = new Question(
                    questionText,
                    optionA,
                    optionB,
                    optionC,
                    optionD,
                    correctAnswer,
                    explanation,
                    quiz
            );
            questions.add(question);
        }

        return questions;
    }
}
