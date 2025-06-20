package com.replan.domain.objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PracticePlan {
    private UUID id;
    private UUID requestId;
    private Map<PracticeFocus, Double> timeAllocation;
    private Integer totalHours;
    private PracticeType practiceType;
    private LocalDateTime generatedAt;
}
