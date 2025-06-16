package com.replan.reviewSession;

import com.replan.business.impl.reviewSession.CreateReviewSessionImpl;
import com.replan.domain.requests.CreateReviewSessionRequest;
import com.replan.domain.responses.CreateReviewSessionResponse;
import com.replan.persistance.ReviewSessionParticipantRepository;
import com.replan.persistance.ReviewSessionRepository;
import com.replan.persistance.TeamMemberRepository;
import com.replan.persistance.entity.ReviewSessionEntity;
import com.replan.persistance.entity.TeamMemberEntity;
import com.replan.persistance.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

class CreateReviewSessionImplTest {
    @Mock
    private ReviewSessionRepository sessionRepository;
    @Mock
    private ReviewSessionParticipantRepository participantRepository;
    @Mock
    private TeamMemberRepository teamMemberRepository;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;
    @InjectMocks
    private CreateReviewSessionImpl subject;

    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID TEAM_ID = UUID.randomUUID();
    private static final UUID SESSION_ID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void happyPath_shouldSaveSessionAndParticipant() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        UserEntity user = new UserEntity();
        user.setId(USER_ID);
        when(authentication.getPrincipal()).thenReturn(user);

        when(teamMemberRepository.findByTeamIdAndUserId(TEAM_ID, USER_ID))
                .thenReturn(Optional.of(new TeamMemberEntity()));

        when(sessionRepository.save(any(ReviewSessionEntity.class))).thenAnswer(inv -> {
            ReviewSessionEntity ent = inv.getArgument(0);
            ent.setId(SESSION_ID);
            return ent;
        });

        var req = new CreateReviewSessionRequest(
                TEAM_ID.toString(), "vid", "title", "desc");

        CreateReviewSessionResponse resp = subject.createSession(req);

        assertThat(resp.getSessionId()).isEqualTo(SESSION_ID.toString());
        assertThat(resp.getTeamId()).isEqualTo(TEAM_ID.toString());
        assertThat(resp.getTitle()).isEqualTo("title");
        verify(sessionRepository).save(argThat(e ->
                e.getTeamId().equals(TEAM_ID) &&
                        e.getVideoUrl().equals("vid") &&
                        e.getTitle().equals("title") &&
                        e.getCreatedBy().equals(USER_ID)
        ));
        verify(participantRepository).save(argThat(p ->
                p.getSessionId().equals(SESSION_ID) &&
                        p.getUserId().equals(USER_ID) &&
                        Boolean.TRUE.equals(p.getIsActive())
        ));
    }

    @Test
    void notTeamMember_shouldThrow() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        UserEntity user = new UserEntity();
        user.setId(USER_ID);
        when(authentication.getPrincipal()).thenReturn(user);
        when(teamMemberRepository.findByTeamIdAndUserId(TEAM_ID, USER_ID))
                .thenReturn(Optional.empty());

        var req = new CreateReviewSessionRequest(
                TEAM_ID.toString(), "vid", "title", "desc");

        assertThatThrownBy(() -> subject.createSession(req))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void missingVideoUrl_shouldThrow() {
        var req = new CreateReviewSessionRequest(TEAM_ID.toString(), "", "t", "d");
        assertThatThrownBy(() -> subject.createSession(req))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void notAuthenticated_shouldThrow() {
        when(securityContext.getAuthentication()).thenReturn(null);
        var req = new CreateReviewSessionRequest(TEAM_ID.toString(), "v", "t", "d");
        assertThatThrownBy(() -> subject.createSession(req))
                .isInstanceOf(AccessDeniedException.class);
    }
}