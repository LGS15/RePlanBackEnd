package com.replan.practice;

import com.replan.business.impl.practice.PracticeTimeCalculatorImpl;
import com.replan.domain.objects.PracticeFocus;
import com.replan.domain.objects.PracticeType;
import com.replan.domain.requests.CalculatePracticeRequest;
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

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PracticeTimeCalculatorImplTest {
    @Mock
    private PracticePlanRequestRepository requestRepository;
    @Mock
    private PracticePlanRepository planRepository;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;
    @InjectMocks
    private PracticeTimeCalculatorImpl subject;

    private static final UUID USER_ID = UUID.randomUUID();

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
    void happyPath_shouldSaveEntitiesAndReturnResponse() {
        mockAuth();
        CalculatePracticeRequest req = new CalculatePracticeRequest(
                PracticeType.INDIVIDUAL,
                List.of(PracticeFocus.AIM_TRAINING, PracticeFocus.GAME_SENSE),
                10,
                null
        );

        when(requestRepository.save(any(PracticePlanRequestEntity.class)))
                .thenAnswer(inv -> {
                    PracticePlanRequestEntity entity = inv.getArgument(0);
                    entity.setId(UUID.randomUUID());
                    return entity;
                });
        when(planRepository.save(any(PracticePlanEntity.class)))
                .thenAnswer(inv -> {
                    PracticePlanEntity entity = inv.getArgument(0);
                    entity.setId(UUID.randomUUID());
                    return entity;
                });

        CalculatePracticeResponse resp = subject.calculatePracticeAllocation(req);

        assertThat(resp.getPracticeType()).isEqualTo(PracticeType.INDIVIDUAL);
        assertThat(resp.getTotalHours()).isEqualTo(10);
        assertThat(resp.getBreakdown()).hasSize(2);

        verify(requestRepository).save(any(PracticePlanRequestEntity.class));
        verify(planRepository).save(any(PracticePlanEntity.class));
    }

    @Test
    void invalidHours_shouldThrow() {
        mockAuth();
        CalculatePracticeRequest req = new CalculatePracticeRequest(
                PracticeType.INDIVIDUAL,
                List.of(PracticeFocus.AIM_TRAINING),
                0,
                null
        );

        assertThatThrownBy(() -> subject.calculatePracticeAllocation(req))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void notAuthenticated_shouldThrow() {
        when(securityContext.getAuthentication()).thenReturn(null);
        CalculatePracticeRequest req = new CalculatePracticeRequest(
                PracticeType.INDIVIDUAL,
                List.of(PracticeFocus.AIM_TRAINING),
                5,
                null
        );

        assertThatThrownBy(() -> subject.calculatePracticeAllocation(req))
                .isInstanceOf(AccessDeniedException.class);
    }
}