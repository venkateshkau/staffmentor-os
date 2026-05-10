package com.staffmentor.study.controller;

import com.staffmentor.study.dto.StudyPlanResponse;
import com.staffmentor.study.service.StudyPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/study-plans")
@RequiredArgsConstructor
public class StudyPlanController {
    private final StudyPlanService service;

    @PostMapping("/generate")
    @ResponseStatus(HttpStatus.CREATED)
    public StudyPlanResponse generate() {
        return service.generateTodayPlan();
    }

    @GetMapping("/today")
    public StudyPlanResponse today() {
        return service.today();
    }
}
