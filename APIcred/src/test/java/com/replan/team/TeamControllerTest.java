package com.replan.team;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.replan.TeamTestConfig;
import com.replan.business.usecases.team.CreateTeamUseCase;
import com.replan.business.usecases.team.GetTeamsByOwnerUseCase;
import com.replan.business.usecases.teamMember.AddTeamMemberUseCase;
import com.replan.controller.TeamController;
import com.replan.domain.objects.Role;
import com.replan.domain.requests.AddTeamMemberRequest;
import com.replan.domain.requests.CreateTeamRequest;
import com.replan.domain.responses.AddTeamMemberResponse;
import com.replan.domain.responses.CreateTeamResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TeamController.class)
@AutoConfigureMockMvc(addFilters = false)    // disable Spring Security filters
@Import(TeamTestConfig.class)
public class TeamControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CreateTeamUseCase createTeamUseCase;

    @Autowired
    private AddTeamMemberUseCase addTeamMemberUseCase;

    @Autowired
    private GetTeamsByOwnerUseCase getTeamsByOwnerUseCase;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCreateTeamEndpoint() throws Exception {
        // Prepare the request and mock response for team creation
        CreateTeamRequest request = new CreateTeamRequest("Alpha Squad", "League of Legends", "owner123");
        CreateTeamResponse response = new CreateTeamResponse("team123", "Alpha Squad", "League of Legends", "owner123");

        when(createTeamUseCase.createTeam(any(CreateTeamRequest.class))).thenReturn(response);

        // POST /teams and verify
        mockMvc.perform(post("/teams")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.teamId").value("team123"))
                .andExpect(jsonPath("$.teamName").value("Alpha Squad"));
    }

    @Test
    public void testAddTeamMemberEndpoint() throws Exception {
        // Prepare request and mock response
        String teamId = "team123";
        AddTeamMemberRequest request = new AddTeamMemberRequest();
        request.setUserId("user456");
        request.setRole(Role.PLAYER);
        request.setTeamId(teamId);

        AddTeamMemberResponse response = new AddTeamMemberResponse("member123", teamId, "user456", Role.PLAYER);

        when(addTeamMemberUseCase.addTeamMember(any(AddTeamMemberRequest.class))).thenReturn(response);

        // POST /teams/{teamId}/members and verify
        mockMvc.perform(post("/teams/" + teamId + "/members")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teamMemberId").value("member123"))
                .andExpect(jsonPath("$.teamId").value(teamId));
    }
}
