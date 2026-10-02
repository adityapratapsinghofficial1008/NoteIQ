package com.noteiq.repository;

import com.noteiq.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface QuizRepository extends JpaRepository<Quiz, UUID> {
    List<Quiz> findByUserId(UUID userId);
    Optional<Quiz> findByIdAndUserId(UUID id, UUID userId);
}
