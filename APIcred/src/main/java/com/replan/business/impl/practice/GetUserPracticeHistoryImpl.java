package com.replan.business.impl.practice;

import com.replan.business.mapper.PracticePlanMapper;
import com.replan.business.usecases.practice.GetUserPracticeHistoryUseCase;
import com.replan.domain.objects.PracticePlan;
import com.replan.domain.responses.CalculatePracticeResponse;
import com.replan.persistance.PracticePlanRepository;
import com.replan.persistance.PracticePlanRequestRepository;
import com.replan.persistance.entity.PracticePlanEntity;
import com.replan.persistance.entity.PracticePlanRequestEntity;
import com.replan.persistance.entity.UserEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class GetUserPracticeHistoryImpl implements GetUserPracticeHistoryUseCase {

    private final PracticePlanRepository planRepository;
    private final PracticePlanRequestRepository requestRepository;

    public GetUserPracticeHistoryImpl(
            PracticePlanRepository planRepository,
            PracticePlanRequestRepository requestRepository
    ) {
        this.planRepository = planRepository;
        this.requestRepository = requestRepository;
    }

    @Override
    public List<CalculatePracticeResponse> getUserPracticeHistory(String userId, Integer limit) {
        UUID userUuid = UUID.fromString(userId);

        // Verify current user can access this data
        UUID currentUserId = getCurrentUserId();
        if (!currentUserId.equals(userUuid)) {
            throw new AccessDeniedException("Cannot access other user's practice history");
        }

        List<PracticePlanEntity> planEntities = planRepository.findByUserIdOrderByGeneratedAtDesc(userUuid);

        if (limit != null && limit > 0) {
            planEntities = planEntities.stream().limit(limit).toList();
        }

        return convertPlanEntitiesToResponses(planEntities);
    }

    private List<CalculatePracticeResponse> convertPlanEntitiesToResponses(List<PracticePlanEntity> planEntities) {
        return planEntities.stream()
                .map(planEntity -> {
                    // Get the corresponding request entity
                    PracticePlanRequestEntity requestEntity = requestRepository.findById(planEntity.getRequestId())
                            .orElseThrow(() -> new IllegalStateException("Request not found for plan: " + planEntity.getId()));

                    // Convert to domain and then to response
                    PracticePlan domain = PracticePlanMapper.fromPlanEntity(planEntity, requestEntity);
                    return PracticePlanMapper.toResponse(domain, Collections.emptyList());
                })
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