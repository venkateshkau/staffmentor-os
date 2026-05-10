package com.staffmentor.coach.repository;

import com.staffmentor.coach.entity.CoachSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CoachSessionRepository
        extends JpaRepository<CoachSession, UUID> {

    Optional<CoachSession>
    findTopByOrderByCreatedAtDesc();
}