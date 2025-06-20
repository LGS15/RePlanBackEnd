package com.replan.business.impl.practice;

import com.replan.business.mapper.PracticePlanMapper;
import com.replan.business.usecases.practice.PracticeTimeCalculatorUseCase;
import com.replan.domain.objects.PracticeFocus;
import com.replan.domain.objects.PracticeFocusInfo;
import com.replan.domain.objects.PracticePlan;
import com.replan.domain.objects.PracticePlanRequest;
import com.replan.domain.requests.CalculatePracticeRequest;
import com.replan.domain.responses.CalculatePracticeResponse;
import com.replan.persistance.PracticePlanRepository;
import com.replan.persistance.PracticePlanRequestRepository;
import com.replan.persistance.entity.PracticePlanEntity;
import com.replan.persistance.entity.PracticePlanRequestEntity;
import com.replan.persistance.entity.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class PracticeTimeCalculatorImpl implements PracticeTimeCalculatorUseCase {

    private final PracticePlanRequestRepository requestRepository;
    private final PracticePlanRepository planRepository;

    public PracticeTimeCalculatorImpl(
            PracticePlanRequestRepository requestRepository,
            PracticePlanRepository planRepository
    ) {
        this.requestRepository = requestRepository;
        this.planRepository = planRepository;
    }

    @Override
    @Transactional
    public CalculatePracticeResponse calculatePracticeAllocation(CalculatePracticeRequest request) {

        validateRequest(request);

        UUID currentUserId = getCurrentUserId();

        PracticePlanRequest planRequest = PracticePlanMapper.fromRequest(request, currentUserId);

        PracticePlanRequestEntity requestEntity = PracticePlanMapper.toRequestEntity(planRequest);
        requestEntity = requestRepository.save(requestEntity);
        planRequest.setId(requestEntity.getId());

        PracticePlan calculatedPlan = performCalculation(planRequest);

        PracticePlanEntity planEntity = PracticePlanMapper.toPlanEntity(calculatedPlan, requestEntity);
        planEntity = planRepository.save(planEntity);
        calculatedPlan.setId(planEntity.getId());

        return PracticePlanMapper.toResponse(calculatedPlan, Collections.emptyList());
    }

    @Override
    public List<PracticeFocusInfo> getAvailableFocuses() {
        return Arrays.stream(PracticeFocus.values())
                .map(PracticePlanMapper::toFocusInfo)
                .toList();
    }

    private PracticePlan performCalculation(PracticePlanRequest request) {
        List<PracticeFocus> focuses = request.getFocusAreas();
        Integer totalHours = request.getAvailableHours();

        Map<PracticeFocus, Double> allocation = new HashMap<>();

        if (!focuses.isEmpty()) {
            allocation.put(focuses.get(0), totalHours * 0.5); // 50% to priority 1
        }
        if (focuses.size() >= 2) {
            allocation.put(focuses.get(1), totalHours * 0.3); // 30% to priority 2
        }
        if (focuses.size() >= 3) {
            allocation.put(focuses.get(2), totalHours * 0.2); // 20% to priority 3
        }

        allocation = applyMinimumTimeConstraints(allocation, totalHours);

        PracticePlan plan = new PracticePlan();
        plan.setRequestId(request.getId());
        plan.setTimeAllocation(allocation);
        plan.setTotalHours(totalHours);
        plan.setPracticeType(request.getPracticeType());
        plan.setGeneratedAt(LocalDateTime.now());

        return plan;
    }

    private Map<PracticeFocus, Double> applyMinimumTimeConstraints(Map<PracticeFocus, Double> allocation, Integer totalHours) {
        double minHoursPerFocus = 0.5; // Minimum 30 minutes per focus
        Map<PracticeFocus, Double> adjustedAllocation = new HashMap<>();

        for (Map.Entry<PracticeFocus, Double> entry : allocation.entrySet()) {
            double hours = Math.max(entry.getValue(), minHoursPerFocus);
            adjustedAllocation.put(entry.getKey(), hours);
        }

        double totalAllocated = adjustedAllocation.values().stream().mapToDouble(Double::doubleValue).sum();
        if (totalAllocated > totalHours) {
            double scaleFactor = totalHours / totalAllocated;
            adjustedAllocation.replaceAll((focus, hours) -> hours * scaleFactor);
        }

        return adjustedAllocation;
    }

    private void validateRequest(CalculatePracticeRequest request) {
        if (request.getAvailableHours() < 1 || request.getAvailableHours() > 50) {
            throw new IllegalArgumentException("Available hours must be between 1 and 50");
        }

        if (request.getFocusAreas() == null || request.getFocusAreas().isEmpty() || request.getFocusAreas().size() > 3) {
            throw new IllegalArgumentException("Must provide 1-3 focus areas");
        }

        Set<PracticeFocus> uniqueFocuses = new HashSet<>(request.getFocusAreas());
        if (uniqueFocuses.size() != request.getFocusAreas().size()) {
            throw new IllegalArgumentException("Focus areas must be unique");
        }

        if (request.getTeamId() != null && !request.getTeamId().trim().isEmpty()) {
            try {
                UUID.fromString(request.getTeamId());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid team ID format");
            }
        }
    }

    private UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserEntity) {
            return ((UserEntity) authentication.getPrincipal()).getId();
        }
        throw new AccessDeniedException("User not authenticated");
    }
}