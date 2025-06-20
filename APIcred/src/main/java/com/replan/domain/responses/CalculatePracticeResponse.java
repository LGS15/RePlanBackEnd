package com.replan.domain.responses;

import com.replan.domain.objects.FocusAllocation;
import com.replan.domain.objects.PracticeFocus;
import com.replan.domain.objects.PracticeType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CalculatePracticeResponse {
    private String planId;
    private PracticeType practiceType;
    private Integer totalHours;
    private Map<PracticeFocus, FocusAllocation> breakdown;
    private LocalDateTime generatedAt;
}
