package com.replan.business.usecases.reviewSession;

import com.replan.domain.requests.JoinSessionRequest;

public interface JoinReviewSessionUseCase {
    void joinSession(JoinSessionRequest request);
}
