package com.replan.practice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.replan.business.usecases.practice.GetPopularCombinationsUseCase;
import com.replan.business.usecases.practice.GetTeamPracticeHistoryUseCase;
import com.replan.business.usecases.practice.GetUserPracticeHistoryUseCase;
import com.replan.business.usecases.practice.PracticeTimeCalculatorUseCase;
import com.replan.domain.objects.PracticeFocus;
import com.replan.domain.objects.PracticeFocusInfo;
import com.replan.domain.objects.PracticeType;
import com.replan.domain.requests.CalculatePracticeRequest;
import com.replan.domain.responses.CalculatePracticeResponse;
import com.replan.domain.responses.PopularCombinationResponse;
import com.replan.domain.responses.PopularCombinationsResponse;
import com.replan.persistance.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
        "spring.main.allow-bean-definition-overriding=true"
})
@ActiveProfiles("test")
public class PracticeCalculatorControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PracticeTimeCalculatorUseCase calculatorUseCase;
    @MockitoBean
    private GetTeamPracticeHistoryUseCase teamHistoryUseCase;
    @MockitoBean
    private GetUserPracticeHistoryUseCase userHistoryUseCase;
    @MockitoBean
    private GetPopularCombinationsUseCase popularCombinationsUseCase;

    private SecurityContext securityContext;
    private Authentication authentication;

    @BeforeEach
    void resetMocks() {
        reset(calculatorUseCase, teamHistoryUseCase, userHistoryUseCase, popularCombinationsUseCase);
        securityContext = Mockito.mock(SecurityContext.class);
        authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void calculatePlan_shouldReturnCreated() throws Exception {
        CalculatePracticeRequest request = new CalculatePracticeRequest(
                PracticeType.INDIVIDUAL,
                List.of(PracticeFocus.AIM_TRAINING),
                10,
                null
        );
        CalculatePracticeResponse response = new CalculatePracticeResponse(
                UUID.randomUUID().toString(),
                PracticeType.INDIVIDUAL,
                10,
                Map.of(),
                LocalDateTime.now()
        );
        when(calculatorUseCase.calculatePracticeAllocation(any(CalculatePracticeRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/practice-calculator/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.planId").value(response.getPlanId()));
    }

    @Test
    void getFocuses_shouldReturnList() throws Exception {
        PracticeFocusInfo info = new PracticeFocusInfo("AIM_TRAINING", "Aim", "desc");
        when(calculatorUseCase.getAvailableFocuses()).thenReturn(List.of(info));

        mockMvc.perform(get("/practice-calculator/focuses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].value").value("AIM_TRAINING"));
    }

    @Test
    void getCurrentUserHistory_shouldReturnList() throws Exception {
        String userId = UUID.randomUUID().toString();
        CalculatePracticeResponse resp = new CalculatePracticeResponse(
                UUID.randomUUID().toString(),
                PracticeType.INDIVIDUAL,
                5,
                Map.of(),
                LocalDateTime.now()
        );
        when(securityContext.getAuthentication()).thenReturn(authentication);
        UserEntity user = new UserEntity();
        user.setId(UUID.fromString(userId));
        when(authentication.getPrincipal()).thenReturn(user);
        when(userHistoryUseCase.getUserPracticeHistory(userId,0, 10)).thenReturn(List.of(resp));

        mockMvc.perform(get("/practice-calculator/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].planId").value(resp.getPlanId()));
    }

    @Test
    void getUserHistory_shouldReturnList() throws Exception {
        String userId = UUID.randomUUID().toString();
        CalculatePracticeResponse resp = new CalculatePracticeResponse(
                UUID.randomUUID().toString(),
                PracticeType.INDIVIDUAL,
                5,
                Map.of(),
                LocalDateTime.now()
        );
        when(userHistoryUseCase.getUserPracticeHistory(userId,0, 10)).thenReturn(List.of(resp));

        mockMvc.perform(get("/practice-calculator/history/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].planId").value(resp.getPlanId()));
    }

    @Test
    void getTeamHistory_shouldReturnList() throws Exception {
        String teamId = UUID.randomUUID().toString();
        CalculatePracticeResponse resp = new CalculatePracticeResponse(
                UUID.randomUUID().toString(),
                PracticeType.TEAM,
                5,
                Map.of(),
                LocalDateTime.now()
        );
        when(teamHistoryUseCase.getTeamPracticeHistory(teamId,0, 10)).thenReturn(List.of(resp));

        mockMvc.perform(get("/practice-calculator/team/{teamId}/history", teamId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].planId").value(resp.getPlanId()));
    }

    @Test
    void getPopularCombinations_shouldReturnResponse() throws Exception {
        PopularCombinationsResponse resp = new PopularCombinationsResponse(
                new PopularCombinationResponse(PracticeType.INDIVIDUAL, List.of(PracticeFocus.AIM_TRAINING), 5),
                new PopularCombinationResponse(PracticeType.TEAM, List.of(PracticeFocus.TEAM_COORDINATION), 3)
        );
        when(popularCombinationsUseCase.getPopularCombinations()).thenReturn(resp);

        mockMvc.perform(get("/practice-calculator/popular"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.individual.focuses[0]").value("AIM_TRAINING"))
                .andExpect(jsonPath("$.team.focuses[0]").value("TEAM_COORDINATION"));
    }
}
