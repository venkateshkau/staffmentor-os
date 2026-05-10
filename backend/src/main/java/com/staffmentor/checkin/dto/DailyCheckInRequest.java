package com.staffmentor.checkin.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record DailyCheckInRequest(
        LocalDate checkinDate,
        String studiedYesterday,
        @NotNull @Min(15) Integer availableMinutes,
        @NotNull @Min(1) @Max(5) Integer energyLevel,
        String blockers,
        String upcomingInterviews,
        String priorityGoal
) {}
