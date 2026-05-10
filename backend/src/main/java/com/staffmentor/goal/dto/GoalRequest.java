package com.staffmentor.goal.dto;

import com.staffmentor.goal.entity.GoalStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record GoalRequest(
        @NotBlank String title,
        String description,
        GoalStatus status,
        @Min(0) @Max(5) Integer priority,
        LocalDate targetDate
) {}
