package com.noteiq.repository;

import com.noteiq.entity.QuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, UUID> {
    List<QuizAttempt> findByUserIdOrderByCompletedAtDesc(UUID userId);
    Optional<QuizAttempt> findByIdAndUserId(UUID id, UUID userId);
}
