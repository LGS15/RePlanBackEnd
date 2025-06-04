package com.replan.business.impl.reviewSession;

import com.replan.business.mapper.ReviewSessionMapper;
import com.replan.business.usecases.reviewSession.GetActiveSessionsUseCase;
import com.replan.domain.objects.SessionStatus;
import com.replan.domain.responses.ReviewSessionResponse;
import com.replan.persistance.ReviewSessionRepository;
import com.replan.persistance.TeamMemberRepository;
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

    public GetActiveSessionsImpl(ReviewSessionRepository reviewSessionRepository,
                                 TeamMemberRepository teamMemberRepository) {
        this.reviewSessionRepository = reviewSessionRepository;
        this.teamMemberRepository = teamMemberRepository;
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
                .map(ReviewSessionMapper::toResponse)
                .toList();
    }

    private UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserEntity) {
            return ((UserEntity) authentication.getPrincipal()).getId();
        }
        throw new AccessDeniedException("User not authenticated");
    }
}
