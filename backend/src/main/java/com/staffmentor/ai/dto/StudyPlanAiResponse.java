package com.staffmentor.ai.dto;

public record StudyPlanAiResponse(
        String mainTopic,
        String whyItMatters,
        String studyTask,
        String codingTask,
        String staffReflectionQuestion,
        String expectedOutput,
        String suggestedCalendarBlock,
        Integer estimatedMinutes,
        String rawResponse
) {}
