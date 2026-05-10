package com.staffmentor.coach.repository;

import com.staffmentor.coach.entity.FocusSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FocusSessionRepository extends JpaRepository<FocusSession, UUID> {
}
