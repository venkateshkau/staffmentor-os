package com.staffmentor.skill.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

public record SkillRequest(
        @NotBlank String name,
        String category,
        @Min(1) @Max(5) Integer currentLevel,
        @Min(1) @Max(5) Integer targetLevel,
        @Min(1) @Max(5) Integer confidenceScore,
        LocalDate lastPracticedDate,
        String notes
) {}
