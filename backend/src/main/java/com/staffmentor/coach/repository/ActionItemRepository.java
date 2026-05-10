package com.staffmentor.coach.repository;

import com.staffmentor.coach.entity.ActionItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ActionItemRepository extends JpaRepository<ActionItem, UUID> {
}
