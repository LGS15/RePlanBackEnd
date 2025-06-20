package com.replan.practice;

import com.replan.business.impl.practice.GetUserPracticeHistoryImpl;
import com.replan.domain.objects.PracticeFocus;
import com.replan.domain.objects.PracticeType;
import com.replan.domain.responses.CalculatePracticeResponse;
import com.replan.persistance.PracticePlanRepository;
import com.replan.persistance.PracticePlanRequestRepository;
import com.replan.persistance.entity.PracticePlanEntity;
import com.replan.persistance.entity.PracticePlanRequestEntity;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GetUserPracticeHistoryImplTest {
    @Mock
    private PracticePlanRepository planRepository;
    @Mock
    private PracticePlanRequestRepository requestRepository;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;
    @InjectMocks
    private GetUserPracticeHistoryImpl subject;

    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PLAN_ID = UUID.randomUUID();
    private static final UUID REQUEST_ID = UUID.randomUUID();

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
    void happyPath_shouldReturnList() {
        mockAuth();
        PracticePlanEntity planEntity = new PracticePlanEntity();
        planEntity.setId(PLAN_ID);
        planEntity.setRequestId(REQUEST_ID);
        planEntity.setTotalHours(5);
        planEntity.setPracticeType(PracticeType.INDIVIDUAL);
        planEntity.setGeneratedAt(LocalDateTime.now());

        PracticePlanRequestEntity requestEntity = new PracticePlanRequestEntity();
        requestEntity.setId(REQUEST_ID);
        requestEntity.setUserId(USER_ID);
        requestEntity.setPracticeType(PracticeType.INDIVIDUAL);
        requestEntity.setAvailableHours(5);
        requestEntity.setCreatedAt(LocalDateTime.now());
        requestEntity.setFocusOne(PracticeFocus.AIM_TRAINING);

        Pageable pageable = PageRequest.of(0, 10);
        when(planRepository.findByUserIdOrderByGeneratedAtDesc(USER_ID, pageable))
                .thenReturn(new PageImpl<>(List.of(planEntity)));
        when(requestRepository.findById(REQUEST_ID))
                .thenReturn(Optional.of(requestEntity));

        List<CalculatePracticeResponse> list = subject.getUserPracticeHistory(USER_ID.toString(), 0, 10);

        assertThat(list).hasSize(1);
        assertThat(list.get(0).getPlanId()).isEqualTo(PLAN_ID.toString());
    }

    @Test
    void wrongUser_shouldThrow() {
        mockAuth();
        UUID other = UUID.randomUUID();
        assertThatThrownBy(() -> subject.getUserPracticeHistory(other.toString(), null, null))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void notAuthenticated_shouldThrow() {
        when(securityContext.getAuthentication()).thenReturn(null);
        assertThatThrownBy(() -> subject.getUserPracticeHistory(USER_ID.toString(), null, null))
                .isInstanceOf(AccessDeniedException.class);
    }
}