package com.replan.business.impl.reviewSession;

import com.replan.business.usecases.reviewSession.LeaveReviewSessionUseCase;
import com.replan.domain.requests.LeaveSessionRequest;
import com.replan.domain.responses.LeaveSessionResponse;
import com.replan.persistance.ReviewSessionParticipantRepository;
import com.replan.persistance.entity.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class LeaveReviewSessionImpl implements LeaveReviewSessionUseCase {

    private final ReviewSessionParticipantRepository participantRepository;

    public LeaveReviewSessionImpl(ReviewSessionParticipantRepository participantRepository) {
        this.participantRepository = participantRepository;
    }

    @Transactional
    @Override
    public LeaveSessionResponse leaveSession(LeaveSessionRequest request) {
        if (request.getSessionId() == null || request.getSessionId().isEmpty()) {
            throw new IllegalArgumentException("Session ID cannot be empty");
        }

        UUID sessionId = UUID.fromString(request.getSessionId());
        UUID currentUserId = getCurrentUserId();

        participantRepository.markParticipantAsLeft(sessionId, currentUserId, LocalDateTime.now());

        return new LeaveSessionResponse(
                request.getSessionId(),
                "Successfully left session",
                true
        );
    }

    private UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserEntity) {
            return ((UserEntity) authentication.getPrincipal()).getId();
        }
        throw new AccessDeniedException("User not authenticated");
    }
}
