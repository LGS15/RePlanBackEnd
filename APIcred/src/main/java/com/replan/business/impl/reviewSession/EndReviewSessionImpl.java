package com.replan.business.impl.reviewSession;

import com.replan.business.usecases.reviewSession.EndReviewSessionUseCase;
import com.replan.domain.objects.SessionStatus;
import com.replan.persistance.ReviewSessionParticipantRepository;
import com.replan.persistance.ReviewSessionRepository;
import com.replan.persistance.TeamMemberRepository;
import com.replan.persistance.entity.ReviewSessionEntity;
import com.replan.persistance.entity.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class EndReviewSessionImpl implements EndReviewSessionUseCase {

    private final ReviewSessionRepository reviewSessionRepository;
    private final ReviewSessionParticipantRepository participantRepository;
    private final TeamMemberRepository teamMemberRepository;

    public EndReviewSessionImpl(ReviewSessionRepository reviewSessionRepository,
                                ReviewSessionParticipantRepository participantRepository,
                                TeamMemberRepository teamMemberRepository) {
        this.reviewSessionRepository = reviewSessionRepository;
        this.participantRepository = participantRepository;
        this.teamMemberRepository = teamMemberRepository;
    }

    @Override
    @Transactional
    public void endSession(String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            throw new IllegalArgumentException("Session ID cannot be empty");
        }

        UUID sessionUuid = UUID.fromString(sessionId);
        UUID currentUserId = getCurrentUserId();

        ReviewSessionEntity session = reviewSessionRepository.findById(sessionUuid)
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));

        boolean isCreator = session.getCreatedBy().equals(currentUserId);
        boolean isMember = teamMemberRepository.findByTeamIdAndUserId(session.getTeamId(), currentUserId).isPresent();

        if (!isCreator && !isMember) {
            throw new AccessDeniedException("You don't have permission to end this session");
        }

        session.setStatus(SessionStatus.ENDED);
        session.setEndedAt(LocalDateTime.now());
        reviewSessionRepository.save(session);

        participantRepository.markParticipantAsLeft(sessionUuid, currentUserId, LocalDateTime.now());
    }

    private UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserEntity) {
            return ((UserEntity) authentication.getPrincipal()).getId();
        }
        throw new AccessDeniedException("User not authenticated");
    }
}