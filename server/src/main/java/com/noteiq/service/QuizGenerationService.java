package com.noteiq.service;

import com.noteiq.entity.Question;
import com.noteiq.entity.Quiz;

import java.util.List;

public interface QuizGenerationService {

    /**
     * Generates questions based on the extracted PDF text.
     *
     * @param pdfText           Extracted text from the uploaded PDF document
     * @param numberOfQuestions Requested count of questions (5, 10, 15, or 20)
     * @param difficulty        Difficulty level (EASY, MEDIUM, HARD)
     * @param quiz              Parent Quiz entity to associate with each Question
     * @return List of generated Question entities
     */
    List<Question> generateQuestions(String pdfText, int numberOfQuestions, String difficulty, Quiz quiz);
}
