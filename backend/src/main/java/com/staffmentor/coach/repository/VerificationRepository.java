package com.staffmentor.coach.repository;

import com.staffmentor.coach.entity.Verification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VerificationRepository extends JpaRepository<Verification, UUID> {
}
