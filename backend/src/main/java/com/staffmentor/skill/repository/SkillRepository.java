package com.staffmentor.skill.repository;

import com.staffmentor.skill.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SkillRepository extends JpaRepository<Skill, UUID> {
    List<Skill> findTop10ByOrderByConfidenceScoreAscLastPracticedDateAsc();
}
