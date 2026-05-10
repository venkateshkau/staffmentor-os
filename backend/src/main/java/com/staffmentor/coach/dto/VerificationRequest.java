package com.staffmentor.coach.dto;

import java.util.UUID;

public record VerificationRequest(
        UUID sessionId,
        String submissionText
) {
}
