package com.replan.business.impl.reviewSession;


import com.replan.business.mapper.ReviewSessionMapper;
import com.replan.business.usecases.reviewSession.CreateReviewSessionUseCase;
import com.replan.domain.requests.CreateReviewSessionRequest;
import com.replan.domain.responses.CreateReviewSessionResponse;
import com.replan.persistance.ReviewSessionParticipantRepository;
import com.replan.persistance.ReviewSessionRepository;
import com.replan.persistance.TeamMemberRepository;
import com.replan.persistance.entity.ReviewSessionEntity;
import com.replan.persistance.entity.ReviewSessionParticipantEntity;
import com.replan.persistance.entity.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class CreateReviewSessionImpl  implements CreateReviewSessionUseCase {

    private final ReviewSessionRepository reviewSessionRepository;
    private final ReviewSessionParticipantRepository participantRepository;
    private final TeamMemberRepository teamMemberRepository;

    public CreateReviewSessionImpl(
            ReviewSessionRepository reviewSessionRepository,
            ReviewSessionParticipantRepository reviewSessionParticipantRepository,
            TeamMemberRepository teamMemberRepository){
        this.reviewSessionRepository = reviewSessionRepository;
        this.participantRepository = reviewSessionParticipantRepository;
        this.teamMemberRepository = teamMemberRepository;
    }

    @Override
    @Transactional
    public CreateReviewSessionResponse createSession(CreateReviewSessionRequest request) {
        if (request.getTeamId() == null || request.getTeamId().isEmpty()) {
            throw new IllegalArgumentException("Team ID cannot be empty");
        }
        if (request.getVideoUrl() == null || request.getVideoUrl().isEmpty()) {
            throw new IllegalArgumentException("Video URL cannot be empty");
        }
        if (request.getTitle() == null || request.getTitle().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }

        UUID currentUserId = getCurrentUserId();
        UUID teamId = UUID.fromString(request.getTeamId());

        boolean isMember = teamMemberRepository.findByTeamIdAndUserId(teamId, currentUserId).isPresent();
        if (!isMember) {
            throw new AccessDeniedException("You must be a team member to create a session");
        }

        ReviewSessionEntity session = ReviewSessionMapper.toEntity(request);
        session.setCreatedBy(currentUserId);
        session.setCreatedAt(LocalDateTime.now());

        ReviewSessionEntity savedSession = reviewSessionRepository.save(session);

        ReviewSessionParticipantEntity participant = new ReviewSessionParticipantEntity();
        participant.setSessionId(savedSession.getId());
        participant.setUserId(currentUserId);
        participant.setJoinedAt(LocalDateTime.now());
        participant.setIsActive(true);

        participantRepository.save(participant);

        return ReviewSessionMapper.toCreateResponse(savedSession);
    }

    private UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserEntity) {
            return ((UserEntity) authentication.getPrincipal()).getId();
        }
        throw new AccessDeniedException("User not authenticated");
    }


}
