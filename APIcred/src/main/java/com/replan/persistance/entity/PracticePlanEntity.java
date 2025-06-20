package com.replan.persistance.entity;

import com.replan.business.mapper.UuidToBytesConverter;
import com.replan.domain.objects.PracticeFocus;
import com.replan.domain.objects.PracticeType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table (name = "PracticePlan")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PracticePlanEntity {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Convert(converter = UuidToBytesConverter.class)
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "BINARY(16)")
    private UUID id;

    @Convert(converter = UuidToBytesConverter.class)
    @Column(name = "request_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID requestId;

    @Column(name = "focus_one_hours")
    private BigDecimal focusOneHours;

    @Column(name = "focus_two_hours")
    private BigDecimal focusTwoHours;

    @Column(name = "focus_three_hours")
    private BigDecimal focusThreeHours;

    @Column(name = "total_hours", nullable = false)
    private Integer totalHours;

    @Enumerated(EnumType.STRING)
    @Column(name = "practice_type", nullable = false)
    private PracticeType practiceType;

    @Column(name = "generated_at", nullable = false)
    private LocalDateTime generatedAt;
}