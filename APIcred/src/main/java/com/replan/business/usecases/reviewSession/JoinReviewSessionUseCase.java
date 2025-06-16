package com.replan.business.usecases.reviewSession;

import com.replan.domain.requests.JoinSessionRequest;
import com.replan.domain.responses.JoinSessionResponse;

public interface JoinReviewSessionUseCase {
    JoinSessionResponse joinSession(JoinSessionRequest request);
}
