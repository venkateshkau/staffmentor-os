package com.staffmentor.coach.repository;

import com.staffmentor.coach.entity.KnowledgeSnippet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface KnowledgeSnippetRepository extends JpaRepository<KnowledgeSnippet, UUID> {
    List<KnowledgeSnippet> findByNextReviewDateLessThanEqual(LocalDate date);
}
