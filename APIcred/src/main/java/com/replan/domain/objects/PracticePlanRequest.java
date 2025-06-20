package com.replan.domain.objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PracticePlanRequest {
    private UUID id;
    private UUID userId;
    private UUID teamId;
    private PracticeType practiceType;
    private Integer availableHours;
    private List<PracticeFocus> focusAreas;
    private LocalDateTime createdAt;
}