package com.staffmentor.coach.dto;

import java.time.LocalDate;
import java.util.UUID;

public record KnowledgeSnippetDto(
        UUID id,
        String content,
        LocalDate nextReviewDate,
        Integer intervalDays
) {
}
