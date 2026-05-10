package com.staffmentor.goal.dto;

import com.staffmentor.goal.entity.GoalStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record GoalResponse(
        UUID id,
        String title,
        String description,
        GoalStatus status,
        Integer priority,
        LocalDate targetDate,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
