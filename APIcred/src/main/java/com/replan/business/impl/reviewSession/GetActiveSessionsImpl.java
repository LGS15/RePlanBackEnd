package com.replan.business.impl.reviewSession;

import com.replan.business.mapper.ReviewSessionMapper;
import com.replan.business.usecases.reviewSession.GetActiveSessionsUseCase;
import com.replan.domain.objects.SessionStatus;
import com.replan.domain.responses.ReviewSessionResponse;
import com.replan.persistance.ReviewSessionParticipantRepository;
import com.replan.persistance.ReviewSessionRepository;
import com.replan.persistance.TeamMemberRepository;
import com.replan.persistance.entity.ReviewSessionEntity;
import com.replan.persistance.entity.UserEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class GetActiveSessionsImpl implements GetActiveSessionsUseCase {

    private final ReviewSessionRepository reviewSessionRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final ReviewSessionParticipantRepository participantRepository;

    public GetActiveSessionsImpl(ReviewSessionRepository reviewSessionRepository,
                                 TeamMemberRepository teamMemberRepository,
                                 ReviewSessionParticipantRepository participantRepository) {
        this.reviewSessionRepository = reviewSessionRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.participantRepository = participantRepository;
    }

    @Override
    public List<ReviewSessionResponse> getActiveSessions(String teamId) {
        if (teamId == null || teamId.isEmpty()) {
            throw new IllegalArgumentException("Team ID cannot be empty");
        }

        UUID currentUserId = getCurrentUserId();
        UUID teamUuid = UUID.fromString(teamId);

        boolean isMember = teamMemberRepository.findByTeamIdAndUserId(teamUuid, currentUserId).isPresent();
        if (!isMember) {
            throw new AccessDeniedException("You must be a team member to view sessions");
        }

        return reviewSessionRepository.findByTeamIdAndStatus(teamUuid, SessionStatus.ACTIVE)
                .stream()
                .map(entity -> {
                    Long activeParticipants = participantRepository.countActiveParticipants(entity.getId());
                    return ReviewSessionMapper.toResponseWithParticipantCount(entity, activeParticipants.intValue());
                })
                .toList();
    }

    @Override
    public ReviewSessionResponse getSessionById(String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            throw new IllegalArgumentException("Session ID cannot be empty");
        }

        UUID currentUserId = getCurrentUserId();
        UUID sessionUuid = UUID.fromString(sessionId);

        ReviewSessionEntity session = reviewSessionRepository.findById(sessionUuid)
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));

        boolean isMember = teamMemberRepository.findByTeamIdAndUserId(session.getTeamId(), currentUserId).isPresent();
        if (!isMember) {
            throw new AccessDeniedException("You must be a team member to view this session");
        }

        Long activeParticipants = participantRepository.countActiveParticipants(session.getId());
        return ReviewSessionMapper.toResponseWithParticipantCount(session, activeParticipants.intValue());
    }


    private UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserEntity) {
            return ((UserEntity) authentication.getPrincipal()).getId();
        }
        throw new AccessDeniedException("User not authenticated");
    }
}
