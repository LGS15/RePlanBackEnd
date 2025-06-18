package com.replan.business.usecases.practice;

import com.replan.domain.responses.CalculatePracticeResponse;

import java.util.List;

public interface GetTeamPracticeHistoryUseCase {

    List<CalculatePracticeResponse> getTeamPracticeHistory(String teamId, Integer limit);
}

