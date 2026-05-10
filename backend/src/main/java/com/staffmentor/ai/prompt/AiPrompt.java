package com.staffmentor.ai.prompt;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ai_prompts")
@Getter
@Setter
public class AiPrompt {

    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    private PromptFeature feature;

    @Enumerated(EnumType.STRING)
    private PromptType promptType;

    private Integer version;

    private Boolean active;

    private Boolean deleted;

    private String createdBy;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(columnDefinition = "TEXT")
    private String notes;
}