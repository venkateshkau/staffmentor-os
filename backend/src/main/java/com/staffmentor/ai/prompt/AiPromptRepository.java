package com.staffmentor.ai.prompt;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AiPromptRepository
        extends JpaRepository<AiPrompt, UUID> {

    Optional<AiPrompt>
    findByFeatureAndPromptTypeAndActiveTrueAndDeletedFalse(
            PromptFeature feature,
            PromptType promptType
    );

    List<AiPrompt>
    findByFeatureAndPromptTypeAndDeletedFalseOrderByVersionDesc(
            PromptFeature feature,
            PromptType promptType
    );
}