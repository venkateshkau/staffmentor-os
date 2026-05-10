package com.staffmentor.study.repository;

import com.staffmentor.study.entity.StudyPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface StudyPlanRepository extends JpaRepository<StudyPlan, UUID> {
    Optional<StudyPlan> findFirstByPlanDateOrderByCreatedAtDesc(LocalDate planDate);
}
