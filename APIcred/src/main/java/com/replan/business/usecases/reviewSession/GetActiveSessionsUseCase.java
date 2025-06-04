package com.replan.business.usecases.reviewSession;

import com.replan.domain.responses.ReviewSessionResponse;

import java.util.List;

public interface GetActiveSessionsUseCase {
    List<ReviewSessionResponse> getActiveSessions (String teamId);
}
