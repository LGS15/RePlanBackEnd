package com.replan.team;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.replan.TeamTestConfig;

import com.replan.domain.objects.Role;
import com.replan.domain.objects.Team;
import com.replan.domain.objects.TeamMember;
import com.replan.domain.requests.AddTeamMemberRequest;
import com.replan.domain.requests.CreateTeamRequest;

import com.replan.persistance.TeamMemberRepository;
import com.replan.persistance.TeamRepository;
import com.replan.persistance.entity.TeamEntity;
import com.replan.persistance.entity.TeamMemberEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.hamcrest.Matchers.hasSize;

import static org.mockito.Mockito.reset;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;



@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Import(TeamTestConfig.class)
@TestPropertySource(properties = {
        "spring.main.allow-bean-definition-overriding=true"
})
public class TeamControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TeamRepository teamRepository;

    @MockitoBean
    private TeamMemberRepository teamMemberRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void resetMocks() {
        reset(teamRepository, teamMemberRepository);
    }

    @Test
    void testCreateTeamEndpoint() throws Exception {
        // given
        var request = new CreateTeamRequest("Alpha Squad", "League of Legends", "owner123");
        var entity = new TeamEntity();
        entity.setId("team123");
        entity.setTeamName("Alpha Squad");
        entity.setGameName("League of Legends");
        entity.setOwnerId("owner123");

        when(teamRepository.save(any(TeamEntity.class)))
                .thenReturn(entity);

        // when / then
        mockMvc.perform(post("/teams")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.teamId").value("team123"))
                .andExpect(jsonPath("$.teamName").value("Alpha Squad"))
                .andExpect(jsonPath("$.gameName").value("League of Legends"))
                .andExpect(jsonPath("$.ownerId").value("owner123"));
    }

    @Test
    void testAddTeamMemberEndpoint() throws Exception {
        // given
        var teamId = "team123";
        var req = new AddTeamMemberRequest();
        req.setUserId("user456");
        req.setRole(Role.PLAYER);
        // path variable will set teamId

        var teamEntity = new TeamEntity();
        teamEntity.setId(teamId);
        // stub that the team exists:
        when(teamRepository.findById(teamId))
                .thenReturn(Optional.of(teamEntity));

        var tmEntity = new TeamMemberEntity();
        tmEntity.setId("member123");
        tmEntity.setTeamId(teamId);
        tmEntity.setUserId("user456");
        tmEntity.setRole(Role.valueOf("PLAYER"));

        when(teamMemberRepository.save(any(TeamMemberEntity.class)))
                .thenReturn(tmEntity);

        // when / then
        mockMvc.perform(post("/teams/{teamId}/members", teamId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teamMemberId").value("member123"))
                .andExpect(jsonPath("$.teamId").value(teamId))
                .andExpect(jsonPath("$.userId").value("user456"))
                .andExpect(jsonPath("$.role").value("PLAYER"));
    }

    @Test
    void testGetTeamsByOwnerEndpoint() throws Exception {
        // given
        var ownerId = "owner123";
        var entity = new TeamEntity();
        entity.setId("team123");
        entity.setTeamName("Alpha");
        entity.setGameName("LOL");
        entity.setOwnerId(ownerId);

        when(teamRepository.findByOwnerId(ownerId))
                .thenReturn(List.of(entity));

        // when / then
        mockMvc.perform(get("/teams/owner/{ownerId}", ownerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].teamId").value("team123"))
                .andExpect(jsonPath("$[0].teamName").value("Alpha"))
                .andExpect(jsonPath("$[0].gameName").value("LOL"))
                .andExpect(jsonPath("$[0].ownerId").value(ownerId));
    }

    @Test
    void testGetTeamMembersByTeamEndpoint() throws Exception {
        // given
        var teamId = "team123";
        var tm = new TeamMemberEntity();
        tm.setId("member123");
        tm.setTeamId(teamId);
        tm.setUserId("user456");
        tm.setRole(Role.valueOf("PLAYER"));

        when(teamMemberRepository.findByTeamId(teamId))
                .thenReturn(List.of(tm));

        // when / then
        mockMvc.perform(get("/teams/{teamId}/members", teamId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.members[0].teamMemberId").value("member123"))
                .andExpect(jsonPath("$.members[0].teamId").value(teamId))
                .andExpect(jsonPath("$.members[0].userId").value("user456"))
                .andExpect(jsonPath("$.totalCount").value(1));
    }
}
