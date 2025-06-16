package com.replan.business.usecases.reviewSession;

import com.replan.domain.websocket.VideoControlPayload;

public interface SyncSessionUseCase {
    void updateSessionState(String sessionId, Long timestamp, Boolean isPlaying);
    VideoControlPayload getCurrentSessionState(String sessionId);
}
