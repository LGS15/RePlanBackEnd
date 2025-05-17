package com.replan.team;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.replan.TeamTestConfig;

import com.replan.business.impl.teamMember.RemoveTeamMemberImpl;
import com.replan.business.usecases.teamMember.RemoveTeamMemberUseCase;
import com.replan.domain.objects.Role;
import com.replan.domain.objects.Team;
import com.replan.domain.objects.TeamMember;
import com.replan.domain.requests.AddTeamMemberRequest;
import com.replan.domain.requests.CreateTeamRequest;

import com.replan.domain.requests.RemoveTeamMemberRequest;
import com.replan.domain.responses.RemoveTeamMemberResponse;
import com.replan.persistance.TeamMemberRepository;
import com.replan.persistance.TeamRepository;
import com.replan.persistance.UserRepository;
import com.replan.persistance.entity.TeamEntity;
import com.replan.persistance.entity.TeamMemberEntity;
import com.replan.persistance.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Import(TeamTestConfig.class)
@TestPropertySource(properties = {
        "spring.main.allow-bean-definition-overriding=true"
})
@ActiveProfiles("test")
public class TeamControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TeamRepository teamRepository;

    @MockitoBean
    private TeamMemberRepository teamMemberRepository;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private RemoveTeamMemberUseCase removeTeamMemberUseCase;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void resetMocks() {
        reset(teamRepository, teamMemberRepository,userRepository);
    }

    @Test
    void testCreateTeamEndpoint() throws Exception {
        String ownerId = UUID.randomUUID().toString();

        var request = new CreateTeamRequest("Alpha Squad", "League of Legends", ownerId);

        // Mock the user repository to return a user for the owner ID
        var userEntity = new UserEntity();
        userEntity.setId(ownerId);
        userEntity.setUsername("testOwner");
        userEntity.setEmail("owner@example.com");
        when(userRepository.findById(UUID.fromString(ownerId))).thenReturn(Optional.of(userEntity));

        // Mock the team repository
        var entity = new TeamEntity();
        entity.setId("team123");
        entity.setTeamName("Alpha Squad");
        entity.setGameName("League of Legends");
        entity.setOwnerId(ownerId);
        when(teamRepository.save(any(TeamEntity.class))).thenReturn(entity);

        // Mock the team member repository to return for the owner
        var teamMemberEntity = new TeamMemberEntity();
        teamMemberEntity.setId("member123");
        teamMemberEntity.setTeamId("team123");
        teamMemberEntity.setUserId(ownerId);
        teamMemberEntity.setRole(Role.OWNER);
        when(teamMemberRepository.save(any(TeamMemberEntity.class))).thenReturn(teamMemberEntity);

        // when / then
        mockMvc.perform(post("/teams")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.teamId").value("team123"))
                .andExpect(jsonPath("$.teamName").value("Alpha Squad"))
                .andExpect(jsonPath("$.gameName").value("League of Legends"))
                .andExpect(jsonPath("$.ownerId").value(ownerId));
    }

    @Test
    void testAddTeamMemberEndpoint() throws Exception {
        String teamId = "team123";
        String userId = UUID.randomUUID().toString();

        var req = new AddTeamMemberRequest();
        req.setEmail("user@example.com");
        req.setRole(Role.PLAYER);

        var teamEntity = new TeamEntity();
        teamEntity.setId(teamId);

        when(teamRepository.findById(teamId))
                .thenReturn(Optional.of(teamEntity));

        var userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setEmail("user@example.com");
        when(userRepository.findByEmail("user@example.com"))
                .thenReturn(Optional.of(userEntity));

        var tmEntity = new TeamMemberEntity();
        tmEntity.setId("member123");
        tmEntity.setTeamId(teamId);
        tmEntity.setUserId(userId);
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
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.role").value("PLAYER"));
    }

    @Test
    void testGetTeamsByOwnerEndpoint() throws Exception {
        String ownerId = UUID.randomUUID().toString();

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
        String teamId = "team123";
        String userId = UUID.randomUUID().toString();

        var tm = new TeamMemberEntity();
        tm.setId("member123");
        tm.setTeamId(teamId);
        tm.setUserId(userId);
        tm.setRole(Role.valueOf("PLAYER"));

        when(teamMemberRepository.findByTeamId(teamId))
                .thenReturn(List.of(tm));

        // when / then
        mockMvc.perform(get("/teams/{teamId}/members", teamId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.members[0].teamMemberId").value("member123"))
                .andExpect(jsonPath("$.members[0].teamId").value(teamId))
                .andExpect(jsonPath("$.members[0].userId").value(userId))
                .andExpect(jsonPath("$.totalCount").value(1));
    }

    @Test
    void testRemoveTeamMemberEndpoint() throws Exception {
        // given
        var teamId = "team123";
        String userId = UUID.randomUUID().toString();
        String ownerId = UUID.randomUUID().toString();

        var team = new TeamEntity();
        team.setId(teamId);
        team.setOwnerId(ownerId);
        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));

        var teamMember = new TeamMemberEntity();
        teamMember.setId("member123");
        teamMember.setTeamId(teamId);
        teamMember.setUserId(userId);
        teamMember.setRole(Role.PLAYER);
        when(teamMemberRepository.findByTeamIdAndUserId(teamId, userId))
                .thenReturn(Optional.of(teamMember));

        // Mock the response from the use case to avoid the authentication check
        RemoveTeamMemberResponse expectedResponse = new RemoveTeamMemberResponse(
                "member123", teamId, userId, Role.PLAYER, true);

        when(removeTeamMemberUseCase.removeTeamMember(any(RemoveTeamMemberRequest.class)))
                .thenReturn(expectedResponse);

        // when / then
        mockMvc.perform(delete("/teams/{teamId}/members/{userId}", teamId, userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teamMemberId").value("member123"))
                .andExpect(jsonPath("$.teamId").value(teamId))
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.removed").value(true));
    }
}