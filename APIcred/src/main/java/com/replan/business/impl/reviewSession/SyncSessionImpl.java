package com.replan.business.impl.reviewSession;

import com.replan.business.usecases.reviewSession.SyncSessionUseCase;
import com.replan.domain.websocket.VideoControlPayload;
import com.replan.persistance.ReviewSessionRepository;
import com.replan.persistance.entity.ReviewSessionEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class SyncSessionImpl implements SyncSessionUseCase {

    private final ReviewSessionRepository reviewSessionRepository;

    public SyncSessionImpl(ReviewSessionRepository reviewSessionRepository) {
        this.reviewSessionRepository = reviewSessionRepository;
    }

    @Override
    @Transactional
    public void updateSessionState(String sessionId, Long timestamp, Boolean isPlaying){
        ReviewSessionEntity session = reviewSessionRepository.findById(UUID.fromString(sessionId))
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));

        session.setCurrentTimestamp(timestamp);
        session.setIsPlaying(isPlaying);

        reviewSessionRepository.save(session);
    }

    @Override
    public VideoControlPayload getCurrentSessionState(String sessionId){
        ReviewSessionEntity session = reviewSessionRepository.findById(UUID.fromString(sessionId))
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));

        return new VideoControlPayload(session.getCurrentTimestamp(), session.getIsPlaying());
    }
}
