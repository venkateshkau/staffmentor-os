package com.staffmentor.coach.dto;

import java.util.UUID;

public record ActionItemDto(
        UUID id,
        String description,
        Boolean completed
) {
}
