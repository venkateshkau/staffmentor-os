package com.staffmentor.project.controller;

import com.staffmentor.project.dto.SprintRequest;
import com.staffmentor.project.dto.SprintResponse;
import com.staffmentor.project.service.SprintService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sprints")
@RequiredArgsConstructor
public class SprintController {

    private final SprintService sprintService;

    @PostMapping("/analyze-jd")
    public SprintResponse analyzeJd(@RequestBody SprintRequest request) {
        return sprintService.analyzeAndCreateSprint(request);
    }

    @org.springframework.web.bind.annotation.GetMapping
    public java.util.List<SprintResponse> getActiveSprints() {
        return sprintService.getActiveSprints();
    }
}
