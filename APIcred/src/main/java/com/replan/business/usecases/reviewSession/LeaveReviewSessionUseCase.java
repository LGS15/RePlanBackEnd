package com.replan.business.usecases.reviewSession;

import com.replan.domain.requests.LeaveSessionRequest;
import com.replan.domain.responses.LeaveSessionResponse;

public interface LeaveReviewSessionUseCase {
    LeaveSessionResponse leaveSession(LeaveSessionRequest request);
}
