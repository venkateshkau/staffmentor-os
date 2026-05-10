package com.staffmentor.project.dto;

import java.util.List;
import java.util.UUID;

public record AiSprintEvaluationResponse(
        List<String> newGoalTitles,
        List<UUID> pausedGoalIds
) {
}
