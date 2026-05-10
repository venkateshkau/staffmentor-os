package com.staffmentor.coach.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "coach_sessions")
@Getter
@Setter
public class CoachSession {

    @Id
    private UUID id;

    private LocalDateTime createdAt;

    private Integer energyLevel;

    private Integer availableMinutes;

    private Boolean completedYesterday;

    @Column(columnDefinition = "TEXT")
    private String blockers;

    @Column(columnDefinition = "TEXT")
    private String upcomingInterview;

    @Column(columnDefinition = "TEXT")
    private String additionalNotes;

    @Column(columnDefinition = "TEXT")
    private String todayFocus;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<ActionItem> actionItems = new java.util.ArrayList<>();

    @OneToOne(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    private Verification verification;

    @Column(columnDefinition = "TEXT")
    private String calendarSuggestions;

    @Column(columnDefinition = "TEXT")
    private String followUpQuestion;

    @Column(columnDefinition = "TEXT")
    private String motivation;

    private Boolean completed;
}