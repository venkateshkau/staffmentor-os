package com.staffmentor.ai.prompt;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PromptService {

    private final AiPromptRepository repository;

    public String getActivePrompt(
            PromptFeature feature,
            PromptType promptType
    ) {

        return repository
                .findByFeatureAndPromptTypeAndActiveTrueAndDeletedFalse(
                        feature,
                        promptType
                )
                .map(AiPrompt::getContent)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "No active prompt found for "
                                        + feature
                                        + " "
                                        + promptType
                        )
                );
    }

    public List<AiPrompt> getPromptVersions(
            PromptFeature feature,
            PromptType promptType
    ) {

        return repository
                .findByFeatureAndPromptTypeAndDeletedFalseOrderByVersionDesc(
                        feature,
                        promptType
                );
    }

    @Transactional
    public AiPrompt createPrompt(
            PromptFeature feature,
            PromptType promptType,
            String content,
            String notes,
            String createdBy,
            boolean activate
    ) {

        int nextVersion =
                repository
                        .findByFeatureAndPromptTypeAndDeletedFalseOrderByVersionDesc(
                                feature,
                                promptType
                        )
                        .stream()
                        .findFirst()
                        .map(prompt -> prompt.getVersion() + 1)
                        .orElse(1);

        if (activate) {

            repository
                    .findByFeatureAndPromptTypeAndActiveTrueAndDeletedFalse(
                            feature,
                            promptType
                    )
                    .ifPresent(existing -> {
                        existing.setActive(false);
                        existing.setUpdatedAt(LocalDateTime.now());

                        repository.save(existing);
                    });
        }

        AiPrompt prompt = new AiPrompt();

        prompt.setId(UUID.randomUUID());

        prompt.setFeature(feature);
        prompt.setPromptType(promptType);

        prompt.setVersion(nextVersion);

        prompt.setActive(activate);
        prompt.setDeleted(false);

        prompt.setCreatedBy(createdBy);

        prompt.setCreatedAt(LocalDateTime.now());

        prompt.setContent(content);
        prompt.setNotes(notes);

        return repository.save(prompt);
    }

    @Transactional
    public void activatePrompt(UUID id) {

        AiPrompt prompt =
                repository.findById(id)
                        .orElseThrow();

        repository
                .findByFeatureAndPromptTypeAndActiveTrueAndDeletedFalse(
                        prompt.getFeature(),
                        prompt.getPromptType()
                )
                .ifPresent(existing -> {
                    existing.setActive(false);
                    existing.setUpdatedAt(LocalDateTime.now());

                    repository.save(existing);
                });

        prompt.setActive(true);
        prompt.setUpdatedAt(LocalDateTime.now());

        repository.save(prompt);
    }

    @Transactional
    public void softDelete(UUID id) {

        AiPrompt prompt =
                repository.findById(id)
                        .orElseThrow();

        prompt.setDeleted(true);
        prompt.setActive(false);
        prompt.setUpdatedAt(LocalDateTime.now());

        repository.save(prompt);
    }
}