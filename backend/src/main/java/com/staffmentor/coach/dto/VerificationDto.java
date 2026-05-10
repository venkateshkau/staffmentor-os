package com.staffmentor.coach.dto;

import com.staffmentor.coach.entity.MasteryLevel;
import java.time.LocalDateTime;
import java.util.UUID;

public record VerificationDto(
        UUID id,
        String submissionText,
        MasteryLevel masteryLevel,
        Integer confidenceScore,
        String aiFeedback,
        LocalDateTime createdAt
) {
}
