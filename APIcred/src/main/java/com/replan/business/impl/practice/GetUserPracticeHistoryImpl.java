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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    public List<CalculatePracticeResponse> getUserPracticeHistory(String userId, Integer page, Integer limit) {
        UUID userUuid = UUID.fromString(userId);

        UUID currentUserId = getCurrentUserId();
        if (!currentUserId.equals(userUuid)) {
            throw new AccessDeniedException("Cannot access other user's practice history");
        }

        int pageNum = page != null && page >= 0 ? page : 0;
        int size = limit != null && limit > 0 ? limit : 10;
        Pageable pageable = PageRequest.of(pageNum, size);

        List<PracticePlanEntity> planEntities = planRepository
                .findByUserIdOrderByGeneratedAtDesc(userUuid, pageable)
                .getContent();

        return convertPlanEntitiesToResponses(planEntities);
    }

    private List<CalculatePracticeResponse> convertPlanEntitiesToResponses(List<PracticePlanEntity> planEntities) {
        return planEntities.stream()
                .map(planEntity -> {

                    PracticePlanRequestEntity requestEntity = requestRepository.findById(planEntity.getRequestId())
                            .orElseThrow(() -> new IllegalStateException("Request not found for plan: " + planEntity.getId()));

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