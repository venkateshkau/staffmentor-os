package com.staffmentor.checkin.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record DailyCheckInResponse(
        UUID id,
        LocalDate checkinDate,
        String studiedYesterday,
        Integer availableMinutes,
        Integer energyLevel,
        String blockers,
        String upcomingInterviews,
        String priorityGoal,
        LocalDateTime createdAt
) {}
