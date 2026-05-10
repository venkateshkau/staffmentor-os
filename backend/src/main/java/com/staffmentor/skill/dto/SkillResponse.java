package com.staffmentor.skill.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record SkillResponse(
        UUID id,
        String name,
        String category,
        Integer currentLevel,
        Integer targetLevel,
        Integer confidenceScore,
        LocalDate lastPracticedDate,
        String notes,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
