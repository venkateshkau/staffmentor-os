package com.staffmentor.coach.controller;

import com.staffmentor.coach.dto.DailyCoachRequest;
import com.staffmentor.coach.dto.LatestCoachSessionResponse;
import com.staffmentor.coach.service.CoachService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/coach")
@RequiredArgsConstructor
public class CoachController {

    private final CoachService coachService;

    @PostMapping("/daily")
    public LatestCoachSessionResponse daily(
            @RequestBody DailyCoachRequest request) {
        return coachService.generateDailyCoaching(request);
    }

    @GetMapping("/latest")
    public LatestCoachSessionResponse latest() {
        return coachService.getLatestSession();
    }

    @GetMapping("/today")
    public LatestCoachSessionResponse today() {
        return coachService.getTodaySession();
    }

    @PatchMapping("/action-items/{id}/toggle")
    public com.staffmentor.coach.dto.ActionItemDto toggleActionItem(
            @PathVariable java.util.UUID id,
            @RequestParam boolean completed) {
        return coachService.toggleActionItemCompletion(id, completed);
    }
}