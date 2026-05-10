package com.staffmentor.skill.service;

import com.staffmentor.common.exception.ResourceNotFoundException;
import com.staffmentor.skill.dto.SkillRequest;
import com.staffmentor.skill.dto.SkillResponse;
import com.staffmentor.skill.entity.Skill;
import com.staffmentor.skill.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SkillService {
    private final SkillRepository skillRepository;

    @Transactional
    public SkillResponse create(SkillRequest request) {
        Skill skill = Skill.builder()
                .name(request.name())
                .category(request.category())
                .currentLevel(defaultValue(request.currentLevel(), 1))
                .targetLevel(defaultValue(request.targetLevel(), 5))
                .confidenceScore(defaultValue(request.confidenceScore(), 3))
                .lastPracticedDate(request.lastPracticedDate())
                .notes(request.notes())
                .build();
        return toResponse(skillRepository.save(skill));
    }

    @Transactional(readOnly = true)
    public List<SkillResponse> findAll() {
        return skillRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<Skill> findWeakSkillEntities() {
        return skillRepository.findTop10ByOrderByConfidenceScoreAscLastPracticedDateAsc();
    }

    @Transactional
    public SkillResponse update(UUID id, SkillRequest request) {
        Skill skill = getEntity(id);
        skill.setName(request.name());
        skill.setCategory(request.category());
        skill.setCurrentLevel(defaultValue(request.currentLevel(), skill.getCurrentLevel()));
        skill.setTargetLevel(defaultValue(request.targetLevel(), skill.getTargetLevel()));
        skill.setConfidenceScore(defaultValue(request.confidenceScore(), skill.getConfidenceScore()));
        skill.setLastPracticedDate(request.lastPracticedDate());
        skill.setNotes(request.notes());
        return toResponse(skillRepository.save(skill));
    }

    private Skill getEntity(UUID id) {
        return skillRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Skill not found: " + id));
    }

    private Integer defaultValue(Integer value, Integer fallback) {
        return value == null ? fallback : value;
    }

    private SkillResponse toResponse(Skill skill) {
        return new SkillResponse(skill.getId(), skill.getName(), skill.getCategory(), skill.getCurrentLevel(), skill.getTargetLevel(), skill.getConfidenceScore(), skill.getLastPracticedDate(), skill.getNotes(), skill.getCreatedAt(), skill.getUpdatedAt());
    }
}
