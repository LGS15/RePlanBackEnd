package com.replan;

import com.replan.business.mapper.PracticePlanMapper;
import com.replan.domain.objects.*;
import com.replan.domain.requests.CalculatePracticeRequest;
import com.replan.domain.responses.CalculatePracticeResponse;
import com.replan.persistance.entity.PracticePlanEntity;
import com.replan.persistance.entity.PracticePlanRequestEntity;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PracticePlanMapperTest {

    @Test
    void constructorIsPrivate() throws Exception {
        Constructor<PracticePlanMapper> ctor = PracticePlanMapper.class.getDeclaredConstructor();
        ctor.setAccessible(true);
        assertThatThrownBy(ctor::newInstance)
                .hasCauseInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void fromRequest_mapsFieldsCorrectly() {
        UUID userId = UUID.randomUUID();
        CalculatePracticeRequest req = new CalculatePracticeRequest(
                PracticeType.INDIVIDUAL,
                List.of(PracticeFocus.AIM_TRAINING, PracticeFocus.GAME_SENSE),
                5,
                "00000000-0000-0000-0000-000000000001"
        );

        PracticePlanRequest result = PracticePlanMapper.fromRequest(req, userId);

        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getTeamId()).isEqualTo(UUID.fromString(req.getTeamId()));
        assertThat(result.getPracticeType()).isEqualTo(req.getPracticeType());
        assertThat(result.getAvailableHours()).isEqualTo(req.getAvailableHours());
        assertThat(result.getFocusAreas()).isEqualTo(req.getFocusAreas());
        assertThat(result.getCreatedAt()).isNotNull();
    }

    @Test
    void toAndFromRequestEntity_roundTripsProperly() {
        PracticePlanRequest domain = new PracticePlanRequest();
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID teamId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        domain.setId(id);
        domain.setUserId(userId);
        domain.setTeamId(teamId);
        domain.setPracticeType(PracticeType.TEAM);
        domain.setAvailableHours(8);
        domain.setFocusAreas(List.of(
                PracticeFocus.AIM_TRAINING,
                PracticeFocus.GAME_SENSE,
                PracticeFocus.MOVEMENT_MECHANICS
        ));
        domain.setCreatedAt(now);

        PracticePlanRequestEntity entity = PracticePlanMapper.toRequestEntity(domain);
        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getUserId()).isEqualTo(userId);
        assertThat(entity.getTeamId()).isEqualTo(teamId);
        assertThat(entity.getPracticeType()).isEqualTo(PracticeType.TEAM);
        assertThat(entity.getAvailableHours()).isEqualTo(8);
        assertThat(entity.getCreatedAt()).isEqualTo(now);
        assertThat(entity.getFocusOne()).isEqualTo(PracticeFocus.AIM_TRAINING);
        assertThat(entity.getFocusTwo()).isEqualTo(PracticeFocus.GAME_SENSE);
        assertThat(entity.getFocusThree()).isEqualTo(PracticeFocus.MOVEMENT_MECHANICS);

        PracticePlanRequest roundTrip = PracticePlanMapper.fromRequestEntity(entity);
        assertThat(roundTrip).usingRecursiveComparison().isEqualTo(domain);
    }

    @Test
    void toPlanEntity_transfersFieldsAndAllocation() {
        PracticePlan plan = new PracticePlan();
        UUID planId = UUID.randomUUID();
        UUID reqId = UUID.randomUUID();
        LocalDateTime gen = LocalDateTime.now();
        plan.setId(planId);
        plan.setRequestId(reqId);
        plan.setTotalHours(30);
        plan.setPracticeType(PracticeType.INDIVIDUAL);
        plan.setGeneratedAt(gen);
        Map<PracticeFocus, Double> alloc = new HashMap<>();
        alloc.put(PracticeFocus.AIM_TRAINING, 10d);
        alloc.put(PracticeFocus.GAME_SENSE, 5d);
        alloc.put(PracticeFocus.MOVEMENT_MECHANICS, 15d);
        plan.setTimeAllocation(alloc);

        PracticePlanRequestEntity request = new PracticePlanRequestEntity();
        request.setFocusOne(PracticeFocus.AIM_TRAINING);
        request.setFocusTwo(PracticeFocus.GAME_SENSE);
        request.setFocusThree(PracticeFocus.MOVEMENT_MECHANICS);

        PracticePlanEntity entity = PracticePlanMapper.toPlanEntity(plan, request);

        assertThat(entity.getId()).isEqualTo(planId);
        assertThat(entity.getRequestId()).isEqualTo(reqId);
        assertThat(entity.getTotalHours()).isEqualTo(30);
        assertThat(entity.getPracticeType()).isEqualTo(PracticeType.INDIVIDUAL);
        assertThat(entity.getGeneratedAt()).isEqualTo(gen);
        assertThat(entity.getFocusOneHours()).isEqualByComparingTo(BigDecimal.valueOf(10d));
        assertThat(entity.getFocusTwoHours()).isEqualByComparingTo(BigDecimal.valueOf(5d));
        assertThat(entity.getFocusThreeHours()).isEqualByComparingTo(BigDecimal.valueOf(15d));
    }

    @Test
    void fromPlanEntity_buildsDomainWithAllocation() {
        PracticePlanEntity entity = new PracticePlanEntity();
        UUID planId = UUID.randomUUID();
        UUID reqId = UUID.randomUUID();
        LocalDateTime gen = LocalDateTime.now();
        entity.setId(planId);
        entity.setRequestId(reqId);
        entity.setTotalHours(30);
        entity.setPracticeType(PracticeType.INDIVIDUAL);
        entity.setGeneratedAt(gen);
        entity.setFocusOneHours(BigDecimal.valueOf(10));
        entity.setFocusTwoHours(BigDecimal.valueOf(5));
        entity.setFocusThreeHours(BigDecimal.valueOf(15));

        PracticePlanRequestEntity request = new PracticePlanRequestEntity();
        request.setFocusOne(PracticeFocus.AIM_TRAINING);
        request.setFocusTwo(PracticeFocus.GAME_SENSE);
        request.setFocusThree(PracticeFocus.MOVEMENT_MECHANICS);

        PracticePlan domain = PracticePlanMapper.fromPlanEntity(entity, request);

        assertThat(domain.getId()).isEqualTo(planId);
        assertThat(domain.getRequestId()).isEqualTo(reqId);
        assertThat(domain.getTotalHours()).isEqualTo(30);
        assertThat(domain.getPracticeType()).isEqualTo(PracticeType.INDIVIDUAL);
        assertThat(domain.getGeneratedAt()).isEqualTo(gen);

        assertThat(domain.getTimeAllocation()).containsOnlyKeys(
                PracticeFocus.AIM_TRAINING,
                PracticeFocus.GAME_SENSE,
                PracticeFocus.MOVEMENT_MECHANICS
        );
        assertThat(domain.getTimeAllocation().get(PracticeFocus.AIM_TRAINING)).isEqualTo(10d);
        assertThat(domain.getTimeAllocation().get(PracticeFocus.GAME_SENSE)).isEqualTo(5d);
        assertThat(domain.getTimeAllocation().get(PracticeFocus.MOVEMENT_MECHANICS)).isEqualTo(15d);
    }

    @Test
    void toResponse_producesExpectedBreakdown_team() {
        PracticePlan plan = new PracticePlan();
        plan.setId(UUID.randomUUID());
        plan.setRequestId(UUID.randomUUID());
        plan.setTotalHours(10);
        plan.setPracticeType(PracticeType.TEAM);
        plan.setGeneratedAt(LocalDateTime.now());
        Map<PracticeFocus, Double> alloc = new LinkedHashMap<>();
        for (PracticeFocus f : PracticeFocus.values()) {
            alloc.put(f, 1d);
        }
        plan.setTimeAllocation(alloc);

        CalculatePracticeResponse resp = PracticePlanMapper.toResponse(plan, List.of());

        assertThat(resp.getPlanId()).isEqualTo(plan.getId().toString());
        assertThat(resp.getBreakdown()).hasSize(PracticeFocus.values().length);
        FocusAllocation aim = resp.getBreakdown().get(PracticeFocus.AIM_TRAINING);
        assertThat(aim.getHours()).isEqualTo(1d);
        assertThat(aim.getPercentage()).isEqualTo(10);
        assertThat(aim.getSuggestedActivities()).contains("Aim trainers (Aim Lab, Kovaak's)");
    }

    @Test
    void toResponse_handlesIndividualBranches() {
        PracticePlan plan = new PracticePlan();
        plan.setId(UUID.randomUUID());
        plan.setRequestId(UUID.randomUUID());
        plan.setTotalHours(2);
        plan.setPracticeType(PracticeType.INDIVIDUAL);
        plan.setGeneratedAt(LocalDateTime.now());
        Map<PracticeFocus, Double> alloc = new HashMap<>();
        alloc.put(PracticeFocus.POST_PLANT_SCENARIOS, 1d);
        alloc.put(PracticeFocus.SITE_HOLDS, 1d);
        plan.setTimeAllocation(alloc);

        CalculatePracticeResponse resp = PracticePlanMapper.toResponse(plan, List.of());

        FocusAllocation postPlant = resp.getBreakdown().get(PracticeFocus.POST_PLANT_SCENARIOS);
        assertThat(postPlant.getSuggestedActivities()).contains("Solo post-plant positioning");
        FocusAllocation site = resp.getBreakdown().get(PracticeFocus.SITE_HOLDS);
        assertThat(site.getSuggestedActivities()).contains("Solo site hold scenarios");
    }

    @Test
    void toFocusInfo_returnsProperInfo() {
        PracticeFocusInfo info = PracticePlanMapper.toFocusInfo(PracticeFocus.GAME_SENSE);
        assertThat(info.getValue()).isEqualTo("GAME_SENSE");
        assertThat(info.getDisplayName()).isEqualTo("Game Sense");
        assertThat(info.getDescription()).isEqualTo("Develop decision-making and awareness");
    }
}