package com.staffmentor.coach.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record LatestCoachSessionResponse(
        UUID id,
        LocalDateTime createdAt,
        String todayFocus,
        List<ActionItemDto> actionItems,
        List<String> calendarSuggestions,
        String followUpQuestion,
        String motivation,
        Boolean completed,
        VerificationDto verification
) {
}