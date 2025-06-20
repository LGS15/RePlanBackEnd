package com.replan.business.mapper;

import com.replan.domain.objects.*;
import com.replan.domain.responses.CalculatePracticeResponse;
import com.replan.persistance.entity.PracticePlanRequestEntity;
import com.replan.persistance.entity.PracticePlanEntity;
import com.replan.domain.requests.CalculatePracticeRequest;

import java.time.LocalDateTime;
import java.util.*;

public class PracticePlanMapper {

    private PracticePlanMapper() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static PracticePlanRequest fromRequest(CalculatePracticeRequest request, UUID userId) {
        PracticePlanRequest domain = new PracticePlanRequest();
        domain.setUserId(userId);
        domain.setTeamId(request.getTeamId() != null ? UUID.fromString(request.getTeamId()) : null);
        domain.setPracticeType(request.getPracticeType());
        domain.setAvailableHours(request.getAvailableHours());
        domain.setFocusAreas(request.getFocusAreas());
        domain.setCreatedAt(LocalDateTime.now());
        return domain;
    }

    // Convert domain to entity
    public static PracticePlanRequestEntity toRequestEntity(PracticePlanRequest domain) {
        PracticePlanRequestEntity entity = new PracticePlanRequestEntity();
        entity.setId(domain.getId());
        entity.setUserId(domain.getUserId());
        entity.setTeamId(domain.getTeamId());
        entity.setPracticeType(domain.getPracticeType());
        entity.setAvailableHours(domain.getAvailableHours());
        entity.setCreatedAt(domain.getCreatedAt());

        List<PracticeFocus> focuses = domain.getFocusAreas();
        if (!focuses.isEmpty()) entity.setFocusOne(focuses.get(0));
        if (focuses.size() >= 2) entity.setFocusTwo(focuses.get(1));
        if (focuses.size() >= 3) entity.setFocusThree(focuses.get(2));

        return entity;
    }

    public static PracticePlanRequest fromRequestEntity(PracticePlanRequestEntity entity) {
        PracticePlanRequest domain = new PracticePlanRequest();
        domain.setId(entity.getId());
        domain.setUserId(entity.getUserId());
        domain.setTeamId(entity.getTeamId());
        domain.setPracticeType(entity.getPracticeType());
        domain.setAvailableHours(entity.getAvailableHours());
        domain.setCreatedAt(entity.getCreatedAt());

        List<PracticeFocus> focuses = new ArrayList<>();
        if (entity.getFocusOne() != null) focuses.add(entity.getFocusOne());
        if (entity.getFocusTwo() != null) focuses.add(entity.getFocusTwo());
        if (entity.getFocusThree() != null) focuses.add(entity.getFocusThree());
        domain.setFocusAreas(focuses);

        return domain;
    }

    public static PracticePlanEntity toPlanEntity(PracticePlan domain, PracticePlanRequestEntity requestEntity) {
        PracticePlanEntity entity = new PracticePlanEntity();
        entity.setId(domain.getId());
        entity.setRequestId(domain.getRequestId());
        entity.setTotalHours(domain.getTotalHours());
        entity.setPracticeType(domain.getPracticeType());
        entity.setGeneratedAt(domain.getGeneratedAt());

        Map<PracticeFocus, Double> allocation = domain.getTimeAllocation();
        if (requestEntity.getFocusOne() != null && allocation.containsKey(requestEntity.getFocusOne())) {
            entity.setFocusOneHours(allocation.get(requestEntity.getFocusOne()));
        }
        if (requestEntity.getFocusTwo() != null && allocation.containsKey(requestEntity.getFocusTwo())) {
            entity.setFocusTwoHours(allocation.get(requestEntity.getFocusTwo()));
        }
        if (requestEntity.getFocusThree() != null && allocation.containsKey(requestEntity.getFocusThree())) {
            entity.setFocusThreeHours(allocation.get(requestEntity.getFocusThree()));
        }

        return entity;
    }

    public static PracticePlan fromPlanEntity(PracticePlanEntity entity, PracticePlanRequestEntity requestEntity) {
        PracticePlan domain = new PracticePlan();
        domain.setId(entity.getId());
        domain.setRequestId(entity.getRequestId());
        domain.setTotalHours(entity.getTotalHours());
        domain.setPracticeType(entity.getPracticeType());
        domain.setGeneratedAt(entity.getGeneratedAt());

        Map<PracticeFocus, Double> allocation = new HashMap<>();
        if (requestEntity.getFocusOne() != null && entity.getFocusOneHours() != null) {
            allocation.put(requestEntity.getFocusOne(), entity.getFocusOneHours());
        }
        if (requestEntity.getFocusTwo() != null && entity.getFocusTwoHours() != null) {
            allocation.put(requestEntity.getFocusTwo(), entity.getFocusTwoHours());
        }
        if (requestEntity.getFocusThree() != null && entity.getFocusThreeHours() != null) {
            allocation.put(requestEntity.getFocusThree(), entity.getFocusThreeHours());
        }
        domain.setTimeAllocation(allocation);

        return domain;
    }

    public static CalculatePracticeResponse toResponse(PracticePlan domain, List<String> suggestedActivitiesProvider) {
        CalculatePracticeResponse response = new CalculatePracticeResponse();
        response.setPlanId(domain.getId().toString());
        response.setPracticeType(domain.getPracticeType());
        response.setTotalHours(domain.getTotalHours());
        response.setGeneratedAt(domain.getGeneratedAt());

        Map<PracticeFocus, FocusAllocation> breakdown = new HashMap<>();
        domain.getTimeAllocation().forEach((focus, hours) -> {
            FocusAllocation allocation = new FocusAllocation();
            allocation.setHours(hours);
            allocation.setPercentage((int) Math.round((hours / domain.getTotalHours()) * 100));
            allocation.setSuggestedActivities(getSuggestedActivities(focus, domain.getPracticeType()));
            breakdown.put(focus, allocation);
        });
        response.setBreakdown(breakdown);

        return response;
    }

    private static List<String> getSuggestedActivities(PracticeFocus focus, PracticeType type) {
        return switch (focus) {
            case AIM_TRAINING -> List.of(
                    "Aim trainers (Aim Lab, Kovaak's)",
                    "Deathmatch servers",
                    "Tracking and flicking exercises",
                    "Crosshair placement drills"
            );

            case GAME_SENSE -> List.of(
                    "Demo analysis of pro matches",
                    "Decision-making scenarios",
                    "Timing and rotation practice",
                    "Economic decision drills"
            );

            case POST_PLANT_SCENARIOS -> type == PracticeType.TEAM ? List.of(
                    "Team post-plant setups",
                    "Coordinated site retakes",
                    "Post-plant communication drills",
                    "Different bomb site scenarios"
            ) : List.of(
                    "Solo post-plant positioning",
                    "1vX post-plant situations",
                    "Post-plant angle holding",
                    "Retake timing practice"
            );

            case SITE_HOLDS -> type == PracticeType.TEAM ? List.of(
                    "Coordinated site defense",
                    "Rotation timing practice",
                    "Crossfire setups",
                    "Site-specific team strategies"
            ) : List.of(
                    "Anchor positioning practice",
                    "Solo site hold scenarios",
                    "Angle management drills",
                    "Site-specific positioning"
            );

            case TEAM_COORDINATION -> List.of(
                    "Execute practice",
                    "Communication drills",
                    "Team strategy review",
                    "Coordination timing practice"
            );

            case MAP_KNOWLEDGE -> List.of(
                    "Callout memorization",
                    "Angle and position study",
                    "Rotation route practice",
                    "Map-specific strategies"
            );

            case MOVEMENT_MECHANICS -> List.of(
                    "Movement drills",
                    "Peeking practice",
                    "Counter-strafing exercises",
                    "Jump technique practice"
            );

            case STRATEGY_REVIEW -> List.of(
                    "Team strategy analysis",
                    "Pro match breakdowns",
                    "Tactical theory study",
                    "Meta analysis"
            );

            case VOD_ANALYSIS -> List.of(
                    "Personal gameplay review",
                    "Team performance analysis",
                    "Mistake identification",
                    "Improvement planning"
            );

            case COMMUNICATION -> List.of(
                    "Callout practice",
                    "Clear communication drills",
                    "Information prioritization",
                    "Team voice discipline"
            );
        };
    }

    public static PracticeFocusInfo toFocusInfo(PracticeFocus focus) {
        return new PracticeFocusInfo(
                focus.name(),
                focus.getDisplayName(),
                focus.getDescription()
        );
    }
}
