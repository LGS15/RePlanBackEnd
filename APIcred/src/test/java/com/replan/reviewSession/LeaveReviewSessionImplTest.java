package com.replan.reviewSession;

import com.replan.business.impl.reviewSession.LeaveReviewSessionImpl;
import com.replan.domain.requests.LeaveSessionRequest;
import com.replan.domain.responses.LeaveSessionResponse;
import com.replan.persistance.ReviewSessionParticipantRepository;
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

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class LeaveReviewSessionImplTest {
    @Mock
    private ReviewSessionParticipantRepository participantRepository;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;
    @InjectMocks
    private LeaveReviewSessionImpl subject;

    private static final UUID USER_ID = UUID.randomUUID();
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

    @Test
    void happyPath_shouldMarkLeft() {
        mockAuth();
        LeaveSessionResponse resp = subject.leaveSession(new LeaveSessionRequest(SESSION_ID.toString()));
        assertThat(resp.getSuccess()).isTrue();
        verify(participantRepository).markParticipantAsLeft(eq(SESSION_ID), eq(USER_ID), any());
    }

    @Test
    void missingSession_shouldThrow() {
        assertThatThrownBy(() -> subject.leaveSession(new LeaveSessionRequest("")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void notAuthenticated_shouldThrow() {
        when(securityContext.getAuthentication()).thenReturn(null);
        assertThatThrownBy(() -> subject.leaveSession(new LeaveSessionRequest(SESSION_ID.toString())))
                .isInstanceOf(AccessDeniedException.class);
    }
}
