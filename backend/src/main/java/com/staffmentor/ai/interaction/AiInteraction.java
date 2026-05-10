package com.staffmentor.ai.interaction;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ai_interactions")
@Getter
@Setter
public class AiInteraction {

    @Id
    private UUID id;

    private String feature;

    private String model;

    private Boolean success;

    private Integer httpStatus;

    private Long latencyMs;

    private String promptVersion;

    private Integer promptTokens;

    private Integer completionTokens;

    private Integer totalTokens;

    private LocalDateTime createdAt;

    @Column(columnDefinition = "TEXT")
    private String systemPrompt;

    @Column(columnDefinition = "TEXT")
    private String userPrompt;

    @Column(columnDefinition = "TEXT")
    private String requestBody;

    @Column(columnDefinition = "TEXT")
    private String rawResponse;

    @Column(columnDefinition = "TEXT")
    private String parsedResponse;

    @Column(columnDefinition = "TEXT")
    private String errorMessage;
}