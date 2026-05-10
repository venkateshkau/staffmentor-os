package com.staffmentor.coach.dto;

import java.util.List;

public record DailyCoachResponse(
        String todayFocus,
        List<String> actionItems,
        List<String> calendarSuggestions,
        String followUpQuestion,
        String motivation
) {
}