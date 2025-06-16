package com.replan.business.impl.reviewSession;

import com.replan.business.usecases.reviewSession.JoinReviewSessionUseCase;
import com.replan.domain.objects.SessionStatus;
import com.replan.domain.requests.JoinSessionRequest;
import com.replan.domain.responses.JoinSessionResponse;
import com.replan.persistance.ReviewSessionParticipantRepository;
import com.replan.persistance.ReviewSessionRepository;
import com.replan.persistance.TeamMemberRepository;
import com.replan.persistance.entity.ReviewSessionEntity;
import com.replan.persistance.entity.ReviewSessionParticipantEntity;
import com.replan.persistance.entity.UserEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class JoinReviewSessionImpl implements JoinReviewSessionUseCase {

    private final ReviewSessionRepository reviewSessionRepository;
    private final ReviewSessionParticipantRepository participantRepository;
    private final TeamMemberRepository teamMemberRepository;

    public JoinReviewSessionImpl(ReviewSessionRepository reviewSessionRepository,
                                 ReviewSessionParticipantRepository participantRepository,
                                 TeamMemberRepository teamMemberRepository) {
        this.reviewSessionRepository = reviewSessionRepository;
        this.participantRepository = participantRepository;
        this.teamMemberRepository = teamMemberRepository;
    }

    @Override
    public JoinSessionResponse joinSession(JoinSessionRequest request) {
        if (request.getSessionId() == null || request.getSessionId().isEmpty()) {
            throw new IllegalArgumentException("Session ID cannot be empty");
        }

        UUID sessionId = UUID.fromString(request.getSessionId());
        UUID currentUserId = getCurrentUserId();

        ReviewSessionEntity session = reviewSessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));

        if (session.getStatus() != SessionStatus.ACTIVE) {
            throw new IllegalArgumentException("Session is not active");
        }

        boolean isMember = teamMemberRepository.findByTeamIdAndUserId(session.getTeamId(), currentUserId).isPresent();
        if (!isMember) {
            throw new AccessDeniedException("You must be a team member to join this session");
        }

        Optional<ReviewSessionParticipantEntity> existingParticipant =
                participantRepository.findBySessionIdAndUserIdAndIsActive(sessionId, currentUserId, true);

        if (existingParticipant.isPresent()) {
            Long activeCount = participantRepository.countActiveParticipants(sessionId);
            return new JoinSessionResponse(
                    request.getSessionId(),
                    "Already in session",
                    session.getCurrentTimestamp(),
                    session.getIsPlaying(),
                    activeCount.intValue()
            );
        }

        ReviewSessionParticipantEntity participant = new ReviewSessionParticipantEntity();
        participant.setSessionId(sessionId);
        participant.setUserId(currentUserId);
        participant.setJoinedAt(LocalDateTime.now());
        participant.setIsActive(true);

        participantRepository.save(participant);

        Long activeCount = participantRepository.countActiveParticipants(sessionId);

        return new JoinSessionResponse(
                request.getSessionId(),
                "Successfully joined session",
                session.getCurrentTimestamp(),
                session.getIsPlaying(),
                activeCount.intValue()
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
