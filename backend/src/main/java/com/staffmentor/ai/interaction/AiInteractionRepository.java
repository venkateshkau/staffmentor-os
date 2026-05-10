package com.staffmentor.ai.interaction;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AiInteractionRepository extends JpaRepository<AiInteraction, UUID> {
}