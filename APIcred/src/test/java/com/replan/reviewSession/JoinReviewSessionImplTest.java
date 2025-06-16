package com.replan.reviewSession;

import com.replan.business.impl.reviewSession.JoinReviewSessionImpl;
import com.replan.domain.objects.SessionStatus;
import com.replan.domain.requests.JoinSessionRequest;
import com.replan.domain.responses.JoinSessionResponse;
import com.replan.persistance.ReviewSessionParticipantRepository;
import com.replan.persistance.ReviewSessionRepository;
import com.replan.persistance.TeamMemberRepository;
import com.replan.persistance.entity.ReviewSessionEntity;
import com.replan.persistance.entity.ReviewSessionParticipantEntity;
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

class JoinReviewSessionImplTest {
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
    private JoinReviewSessionImpl subject;

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

    private ReviewSessionEntity activeSession() {
        ReviewSessionEntity ent = new ReviewSessionEntity();
        ent.setId(SESSION_ID);
        ent.setTeamId(TEAM_ID);
        ent.setStatus(SessionStatus.ACTIVE);
        ent.setCurrentTimestamp(5L);
        ent.setIsPlaying(true);
        return ent;
    }

    @Test
    void happyPath_newParticipant_shouldJoin() {
        mockAuth();
        when(sessionRepository.findById(SESSION_ID)).thenReturn(Optional.of(activeSession()));
        when(teamMemberRepository.findByTeamIdAndUserId(TEAM_ID, USER_ID))
                .thenReturn(Optional.of(new TeamMemberEntity()));
        when(participantRepository.findBySessionIdAndUserIdAndIsActive(SESSION_ID, USER_ID, true))
                .thenReturn(Optional.empty());
        when(participantRepository.countActiveParticipants(SESSION_ID)).thenReturn(1L);

        JoinSessionResponse resp = subject.joinSession(new JoinSessionRequest(SESSION_ID.toString()));

        assertThat(resp.getMessage()).contains("Successfully");
        verify(participantRepository).save(argThat(p ->
                p.getSessionId().equals(SESSION_ID) &&
                        p.getUserId().equals(USER_ID)
        ));
    }

    @Test
    void alreadyParticipant_shouldReturnExisting() {
        mockAuth();
        when(sessionRepository.findById(SESSION_ID)).thenReturn(Optional.of(activeSession()));
        when(teamMemberRepository.findByTeamIdAndUserId(TEAM_ID, USER_ID))
                .thenReturn(Optional.of(new TeamMemberEntity()));
        when(participantRepository.findBySessionIdAndUserIdAndIsActive(SESSION_ID, USER_ID, true))
                .thenReturn(Optional.of(new ReviewSessionParticipantEntity()));
        when(participantRepository.countActiveParticipants(SESSION_ID)).thenReturn(2L);

        JoinSessionResponse resp = subject.joinSession(new JoinSessionRequest(SESSION_ID.toString()));

        assertThat(resp.getMessage()).contains("Already");
        verify(participantRepository, never()).save(any());
    }

    @Test
    void nonMember_shouldThrow() {
        mockAuth();
        when(sessionRepository.findById(SESSION_ID)).thenReturn(Optional.of(activeSession()));
        when(teamMemberRepository.findByTeamIdAndUserId(TEAM_ID, USER_ID))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> subject.joinSession(new JoinSessionRequest(SESSION_ID.toString())))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void sessionNotActive_shouldThrow() {
        mockAuth();
        ReviewSessionEntity ent = activeSession();
        ent.setStatus(SessionStatus.ENDED);
        when(sessionRepository.findById(SESSION_ID)).thenReturn(Optional.of(ent));
        when(teamMemberRepository.findByTeamIdAndUserId(TEAM_ID, USER_ID))
                .thenReturn(Optional.of(new TeamMemberEntity()));

        assertThatThrownBy(() -> subject.joinSession(new JoinSessionRequest(SESSION_ID.toString())))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void notAuthenticated_shouldThrow() {
        when(securityContext.getAuthentication()).thenReturn(null);
        assertThatThrownBy(() -> subject.joinSession(new JoinSessionRequest(SESSION_ID.toString())))
                .isInstanceOf(AccessDeniedException.class);
    }
}
