package com.replan.business.usecases.practice;

import com.replan.domain.responses.CalculatePracticeResponse;

import java.util.List;

public interface GetUserPracticeHistoryUseCase {

    List<CalculatePracticeResponse> getUserPracticeHistory(String userId, Integer page, Integer limit);
}
