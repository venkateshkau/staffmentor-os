package com.staffmentor.goal.repository;

import com.staffmentor.goal.entity.Goal;
import com.staffmentor.goal.entity.GoalStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface GoalRepository extends JpaRepository<Goal, UUID> {
    List<Goal> findByStatusOrderByPriorityAscUpdatedAtDesc(GoalStatus status);
}
