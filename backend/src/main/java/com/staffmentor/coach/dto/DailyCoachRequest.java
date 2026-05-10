package com.staffmentor.coach.dto;

public record DailyCoachRequest(
        Integer energyLevel,
        Integer availableMinutes,
        Boolean completedYesterday,
        String blockers,
        String upcomingInterview,
        String additionalNotes,
        String workspacePath
) {
}