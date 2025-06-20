package com.replan.persistance.entity;

import com.replan.business.mapper.UuidToBytesConverter;
import com.replan.domain.objects.PracticeFocus;
import com.replan.domain.objects.PracticeType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "PracticePlanRequest")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PracticePlanRequestEntity {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Convert(converter = UuidToBytesConverter.class)
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "BINARY(16)")
    private UUID id;

    @Convert(converter = UuidToBytesConverter.class)
    @Column(name = "user_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID userId;

    @Convert(converter = UuidToBytesConverter.class)
    @Column(name = "team_id", columnDefinition = "BINARY(16)")
    private UUID teamId;

    @Enumerated(EnumType.STRING)
    @Column(name = "practice_type", nullable = false)
    private PracticeType practiceType;

    @Column(name = "available_hours", nullable = false)
    private Integer availableHours;

    @Enumerated(EnumType.STRING)
    @Column(name = "focus_priority_1")
    private PracticeFocus focusOne;

    @Enumerated(EnumType.STRING)
    @Column(name = "focus_priority_2")
    private PracticeFocus focusTwo;

    @Enumerated(EnumType.STRING)
    @Column(name = "focus_priority_3")
    private PracticeFocus focusThree;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}

