package com.replan.business.usecases.reviewSession;

import com.replan.domain.requests.CreateReviewSessionRequest;
import com.replan.domain.responses.CreateReviewSessionResponse;

public interface CreateReviewSessionUseCase {
    CreateReviewSessionResponse createSession(CreateReviewSessionRequest request);
}
