package com.replan.business.usecases.reviewSession;

import com.replan.domain.requests.EndSessionRequest;
import com.replan.domain.responses.EndSessionResponse;

public interface EndReviewSessionUseCase {
    EndSessionResponse endSession(EndSessionRequest request);
}
