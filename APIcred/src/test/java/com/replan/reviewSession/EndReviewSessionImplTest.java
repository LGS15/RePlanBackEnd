package com.replan.reviewSession;

import com.replan.business.impl.reviewSession.EndReviewSessionImpl;
import com.replan.domain.objects.SessionStatus;
import com.replan.domain.requests.EndSessionRequest;
import com.replan.domain.responses.EndSessionResponse;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class EndReviewSessionImplTest {
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
    private EndReviewSessionImpl subject;

    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID TEAM_ID = UUID.randomUUID();
    private static final UUID SESSION_ID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
    }

    private void mockAuth() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        UserEntity user = new UserEntity();
        user.setId(USER_ID);
        when(authentication.getPrincipal()).thenReturn(user);
    }

    private ReviewSessionEntity sessionEntity() {
        ReviewSessionEntity e = new ReviewSessionEntity();
        e.setId(SESSION_ID);
        e.setTeamId(TEAM_ID);
        e.setCreatedBy(USER_ID);
        e.setStatus(SessionStatus.ACTIVE);
        return e;
    }

    @Test
    void creatorEnding_shouldEndSession() {
        mockAuth();
        ReviewSessionEntity ent = sessionEntity();
        when(sessionRepository.findById(SESSION_ID)).thenReturn(Optional.of(ent));
        when(teamMemberRepository.findByTeamIdAndUserId(TEAM_ID, USER_ID))
                .thenReturn(Optional.of(new TeamMemberEntity()));

        EndSessionResponse resp = subject.endSession(new EndSessionRequest(SESSION_ID.toString()));

        assertThat(resp.getSuccess()).isTrue();
        verify(sessionRepository).save(argThat(s -> s.getStatus() == SessionStatus.ENDED));
        verify(participantRepository).markParticipantAsLeft(eq(SESSION_ID), eq(USER_ID), any());
    }

    @Test
    void alreadyEnded_shouldReturnMessage() {
        mockAuth();
        ReviewSessionEntity ent = sessionEntity();
        ent.setStatus(SessionStatus.ENDED);
        when(sessionRepository.findById(SESSION_ID)).thenReturn(Optional.of(ent));
        when(teamMemberRepository.findByTeamIdAndUserId(TEAM_ID, USER_ID))
                .thenReturn(Optional.of(new TeamMemberEntity()));

        EndSessionResponse resp = subject.endSession(new EndSessionRequest(SESSION_ID.toString()));

        assertThat(resp.getSuccess()).isFalse();
        verify(sessionRepository, never()).save(any());
    }

    @Test
    void notMember_shouldThrow() {
        mockAuth();
        ReviewSessionEntity ent = sessionEntity();
        ent.setCreatedBy(UUID.randomUUID());
        when(sessionRepository.findById(SESSION_ID)).thenReturn(Optional.of(ent));
        when(teamMemberRepository.findByTeamIdAndUserId(TEAM_ID, USER_ID))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> subject.endSession(new EndSessionRequest(SESSION_ID.toString())))
                .isInstanceOf(AccessDeniedException.class);
    }



    @Test
    void notAuthenticated_shouldThrow() {
        when(securityContext.getAuthentication()).thenReturn(null);
        assertThatThrownBy(() -> subject.endSession(new EndSessionRequest(SESSION_ID.toString())))
                .isInstanceOf(AccessDeniedException.class);
    }
}