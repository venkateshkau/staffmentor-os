package com.staffmentor.project.repository;

import com.staffmentor.project.entity.Sprint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SprintRepository extends JpaRepository<Sprint, UUID> {
}
