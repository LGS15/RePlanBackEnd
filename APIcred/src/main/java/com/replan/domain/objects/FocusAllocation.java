package com.replan.domain.objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FocusAllocation {
    private Double hours;
    private Integer percentage;
    private List<String> suggestedActivities;
}