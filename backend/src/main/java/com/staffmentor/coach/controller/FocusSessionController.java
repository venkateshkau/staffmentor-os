package com.staffmentor.coach.controller;

import com.staffmentor.coach.entity.ActionItem;
import com.staffmentor.coach.entity.FocusSession;
import com.staffmentor.coach.repository.ActionItemRepository;
import com.staffmentor.coach.repository.FocusSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/focus-sessions")
@RequiredArgsConstructor
public class FocusSessionController {

    private final FocusSessionRepository focusSessionRepository;
    private final ActionItemRepository actionItemRepository;

    public record FocusRequest(UUID actionItemId, int durationMinutes) {}

    @PostMapping
    public void logFocusSession(@RequestBody FocusRequest request) {
        ActionItem actionItem = actionItemRepository.findById(request.actionItemId())
                .orElseThrow(() -> new IllegalArgumentException("ActionItem not found"));
        
        FocusSession session = new FocusSession();
        session.setActionItem(actionItem);
        session.setDurationMinutes(request.durationMinutes());
        focusSessionRepository.save(session);
    }
}
