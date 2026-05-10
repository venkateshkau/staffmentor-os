package com.staffmentor.study.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record StudyPlanResponse(
        UUID id,
        LocalDate planDate,
        String mainTopic,
        String whyItMatters,
        String studyTask,
        String codingTask,
        String staffReflectionQuestion,
        String expectedOutput,
        String suggestedCalendarBlock,
        Integer estimatedMinutes,
        String aiModel,
        LocalDateTime createdAt
) {}
