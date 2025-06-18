package com.replan.domain.requests;

import com.replan.domain.objects.PracticeFocus;
import com.replan.domain.objects.PracticeType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CalculatePracticeRequest {
    private PracticeType practiceType;
    private List<PracticeFocus> focusAreas;
    private Integer availableHours;
    private String teamId;
}