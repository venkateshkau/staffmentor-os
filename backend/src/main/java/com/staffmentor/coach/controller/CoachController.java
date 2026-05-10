package com.staffmentor.coach.controller;

import com.staffmentor.coach.dto.DailyCoachRequest;
import com.staffmentor.coach.dto.DailyCoachResponse;
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
    public DailyCoachResponse daily(
            @RequestBody DailyCoachRequest request
    ) {
        return coachService.generateDailyCoaching(request);
    }

    @GetMapping("/latest")
    public LatestCoachSessionResponse latest() {
        return coachService.getLatestSession();
    }
}