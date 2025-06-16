package com.replan.reviewSession;

import com.replan.business.impl.reviewSession.GetActiveSessionsImpl;
import com.replan.domain.objects.SessionStatus;
import com.replan.domain.responses.ReviewSessionResponse;
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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class GetActiveSessionsImplTest {
    @Mock
    private ReviewSessionRepository sessionRepository;
    @Mock
    private TeamMemberRepository teamMemberRepository;
    @Mock
    private ReviewSessionParticipantRepository participantRepository;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;
    @InjectMocks
    private GetActiveSessionsImpl subject;

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
    void getActiveSessions_shouldReturnList() {
        mockAuth();
        when(teamMemberRepository.findByTeamIdAndUserId(TEAM_ID, USER_ID))
                .thenReturn(Optional.of(new TeamMemberEntity()));
        when(sessionRepository.findByTeamIdAndStatus(TEAM_ID, SessionStatus.ACTIVE))
                .thenReturn(List.of(sessionEntity()));
        when(participantRepository.countActiveParticipants(SESSION_ID)).thenReturn(2L);

        List<ReviewSessionResponse> list = subject.getActiveSessions(TEAM_ID.toString());

        assertThat(list).hasSize(1);
        assertThat(list.get(0).getSessionId()).isEqualTo(SESSION_ID.toString());
    }

    @Test
    void getSessionById_shouldReturnSession() {
        mockAuth();
        ReviewSessionEntity ent = sessionEntity();
        when(sessionRepository.findById(SESSION_ID)).thenReturn(Optional.of(ent));
        when(teamMemberRepository.findByTeamIdAndUserId(TEAM_ID, USER_ID))
                .thenReturn(Optional.of(new TeamMemberEntity()));
        when(participantRepository.countActiveParticipants(SESSION_ID)).thenReturn(3L);

        ReviewSessionResponse resp = subject.getSessionById(SESSION_ID.toString());

        assertThat(resp.getTeamId()).isEqualTo(TEAM_ID.toString());
        assertThat(resp.getActiveParticipants()).isEqualTo(3);
    }

    @Test
    void notTeamMember_shouldThrow() {
        mockAuth();
        when(teamMemberRepository.findByTeamIdAndUserId(TEAM_ID, USER_ID))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> subject.getActiveSessions(TEAM_ID.toString()))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void notAuthenticated_shouldThrow() {
        when(securityContext.getAuthentication()).thenReturn(null);
        assertThatThrownBy(() -> subject.getActiveSessions(TEAM_ID.toString()))
                .isInstanceOf(AccessDeniedException.class);
    }
}