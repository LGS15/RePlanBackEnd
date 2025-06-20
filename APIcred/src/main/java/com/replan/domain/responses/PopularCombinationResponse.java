package com.replan.domain.responses;

import com.replan.domain.objects.PracticeFocus;
import com.replan.domain.objects.PracticeType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PopularCombinationResponse {
    private PracticeType practiceType;
    private List<PracticeFocus> focuses;
    private long count;
}