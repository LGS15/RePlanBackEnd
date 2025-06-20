package com.replan.business.usecases.practice;

import com.replan.domain.objects.PracticeFocusInfo;
import com.replan.domain.requests.CalculatePracticeRequest;
import com.replan.domain.responses.CalculatePracticeResponse;

import java.util.List;

public interface PracticeTimeCalculatorUseCase {

    CalculatePracticeResponse calculatePracticeAllocation(CalculatePracticeRequest request);

    List<PracticeFocusInfo> getAvailableFocuses();

}
