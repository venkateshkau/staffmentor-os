package com.staffmentor.goal.controller;

import com.staffmentor.goal.dto.GoalRequest;
import com.staffmentor.goal.dto.GoalResponse;
import com.staffmentor.goal.service.GoalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/goals")
@RequiredArgsConstructor
public class GoalController {
    private final GoalService goalService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GoalResponse create(@Valid @RequestBody GoalRequest request) {
        return goalService.create(request);
    }

    @GetMapping
    public List<GoalResponse> findAll() {
        return goalService.findAll();
    }

    @GetMapping("/{id}")
    public GoalResponse findById(@PathVariable UUID id) {
        return goalService.findById(id);
    }

    @PutMapping("/{id}")
    public GoalResponse update(@PathVariable UUID id, @Valid @RequestBody GoalRequest request) {
        return goalService.update(id, request);
    }

    @PatchMapping("/{id}/archive")
    public GoalResponse archiveGoal(@PathVariable UUID id) {
        return goalService.archiveGoal(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        goalService.delete(id);
    }
}
