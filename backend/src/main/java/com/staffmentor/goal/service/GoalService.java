package com.staffmentor.goal.service;

import com.staffmentor.common.exception.ResourceNotFoundException;
import com.staffmentor.goal.dto.GoalRequest;
import com.staffmentor.goal.dto.GoalResponse;
import com.staffmentor.goal.entity.Goal;
import com.staffmentor.goal.entity.GoalStatus;
import com.staffmentor.goal.repository.GoalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;

    @Transactional
    public GoalResponse create(GoalRequest request) {
        Goal goal = Goal.builder()
                .title(request.title())
                .description(request.description())
                .status(request.status() == null ? GoalStatus.ACTIVE : request.status())
                .priority(request.priority() == null ? 3 : request.priority())
                .targetDate(request.targetDate())
                .build();
        return toResponse(goalRepository.save(goal));
    }

    @Transactional(readOnly = true)
    public List<GoalResponse> findAll() {
        return goalRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<Goal> findActiveGoalEntities() {
        return goalRepository.findByStatusOrderByPriorityAscUpdatedAtDesc(GoalStatus.ACTIVE);
    }

    @Transactional(readOnly = true)
    public GoalResponse findById(UUID id) {
        return toResponse(getEntity(id));
    }

    @Transactional
    public GoalResponse update(UUID id, GoalRequest request) {
        Goal goal = getEntity(id);
        goal.setTitle(request.title());
        goal.setDescription(request.description());
        if (request.status() != null) goal.setStatus(request.status());
        if (request.priority() != null) goal.setPriority(request.priority());
        goal.setTargetDate(request.targetDate());
        return toResponse(goalRepository.save(goal));
    }
    @Transactional
    public GoalResponse archiveGoal(UUID id) {
        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Goal not found"));

        goal.setStatus(GoalStatus.ARCHIVED);
        goal.setUpdatedAt(LocalDateTime.now());

        return toResponse(goal);
    }

    @Transactional
    public void delete(UUID id) {
        if (!goalRepository.existsById(id)) throw new ResourceNotFoundException("Goal not found: " + id);
        goalRepository.deleteById(id);
    }

    private Goal getEntity(UUID id) {
        return goalRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Goal not found: " + id));
    }

    private GoalResponse toResponse(Goal goal) {
        return new GoalResponse(goal.getId(), goal.getTitle(), goal.getDescription(), goal.getStatus(), goal.getPriority(), goal.getTargetDate(), goal.getCreatedAt(), goal.getUpdatedAt());
    }
}
