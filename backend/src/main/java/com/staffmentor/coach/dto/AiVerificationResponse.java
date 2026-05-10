package com.staffmentor.coach.dto;

import com.staffmentor.coach.entity.MasteryLevel;

public record AiVerificationResponse(
        MasteryLevel masteryLevel,
        Integer confidenceScore,
        String aiFeedback,
        String coreSnippet
) {
}
