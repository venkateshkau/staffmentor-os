package com.staffmentor.coach.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "verifications")
@Getter
@Setter
public class Verification {

    @Id
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false, unique = true)
    private CoachSession session;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String submissionText;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MasteryLevel masteryLevel;

    @Column(nullable = false)
    private Integer confidenceScore;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String aiFeedback;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
