package com.staffmentor.skill.controller;

import com.staffmentor.skill.dto.SkillRequest;
import com.staffmentor.skill.dto.SkillResponse;
import com.staffmentor.skill.service.SkillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/skills")
@RequiredArgsConstructor
public class SkillController {
    private final SkillService skillService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SkillResponse create(@Valid @RequestBody SkillRequest request) {
        return skillService.create(request);
    }

    @GetMapping
    public List<SkillResponse> findAll() {
        return skillService.findAll();
    }

    @PutMapping("/{id}")
    public SkillResponse update(@PathVariable UUID id, @Valid @RequestBody SkillRequest request) {
        return skillService.update(id, request);
    }
}
