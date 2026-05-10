package com.staffmentor.project.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record SprintResponse(
        UUID id,
        String title,
        LocalDate targetDate,
        String status,
        List<GoalDto> goals
) {
    public record GoalDto(UUID id, String title, String status, Integer priority) {}
}
