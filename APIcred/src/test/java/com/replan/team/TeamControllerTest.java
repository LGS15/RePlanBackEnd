package com.replan.team;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.replan.TeamTestConfig;


import com.replan.business.usecases.team.DeleteTeamUseCase;
import com.replan.business.usecases.team.GetTeamsByUserUseCase;
import com.replan.business.usecases.teamMember.RemoveTeamMemberUseCase;
import com.replan.domain.objects.Role;

import com.replan.domain.requests.AddTeamMemberRequest;
import com.replan.domain.requests.CreateTeamRequest;

import com.replan.domain.requests.DeleteTeamRequest;
import com.replan.domain.requests.RemoveTeamMemberRequest;
import com.replan.domain.responses.DeleteTeamResponse;
import com.replan.domain.responses.RemoveTeamMemberResponse;
import com.replan.domain.responses.TeamResponse;
import com.replan.persistance.TeamMemberRepository;
import com.replan.persistance.TeamRepository;
import com.replan.persistance.UserRepository;
import com.replan.persistance.entity.TeamEntity;
import com.replan.persistance.entity.TeamMemberEntity;
import com.replan.persistance.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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

    @MockitoBean
    private GetTeamsByUserUseCase getTeamsByUserUseCase;

    @MockitoBean
    private DeleteTeamUseCase deleteTeamUseCase;

    @Autowired
    private ObjectMapper objectMapper;

    private static final UUID TEAM_UUID = UUID.randomUUID();
    private static final UUID MEMBER_UUID = UUID.randomUUID();
    private static final UUID OWNER_UUID = UUID.randomUUID();
    private static final UUID USER_UUID = UUID.randomUUID();
    private static final UUID NON_EXISTENT_TEAM_UUID = UUID.randomUUID();
    private static final UUID TEAM_UUID_2 = UUID.randomUUID();


    @BeforeEach
    void resetMocks() {
        reset(teamRepository, teamMemberRepository,userRepository);
    }

    @Test
    void testCreateTeamEndpoint() throws Exception {
        String ownerId = OWNER_UUID.toString();

        var request = new CreateTeamRequest("Alpha Squad", "League of Legends", ownerId);

        var userEntity = new UserEntity();
        userEntity.setId(OWNER_UUID);
        userEntity.setUsername("testOwner");
        userEntity.setEmail("owner@example.com");
        when(userRepository.findById(OWNER_UUID)).thenReturn(Optional.of(userEntity));

        // Mock the team repository
        var entity = new TeamEntity();
        entity.setId(TEAM_UUID);
        entity.setTeamName("Alpha Squad");
        entity.setGameName("League of Legends");
        entity.setOwnerId(OWNER_UUID);
        when(teamRepository.save(any(TeamEntity.class))).thenReturn(entity);

        // Mock the team member repository to return for the owner
        var teamMemberEntity = new TeamMemberEntity();
        teamMemberEntity.setId(MEMBER_UUID);
        teamMemberEntity.setTeamId(TEAM_UUID);
        teamMemberEntity.setUserId(OWNER_UUID);
        teamMemberEntity.setRole(Role.OWNER);
        when(teamMemberRepository.save(any(TeamMemberEntity.class))).thenReturn(teamMemberEntity);

        // when / then
        mockMvc.perform(post("/teams")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.teamId").value(TEAM_UUID.toString()))
                .andExpect(jsonPath("$.teamName").value("Alpha Squad"))
                .andExpect(jsonPath("$.gameName").value("League of Legends"))
                .andExpect(jsonPath("$.ownerId").value(ownerId));
    }

    @Test
    void testAddTeamMemberEndpoint() throws Exception {
        String teamId = TEAM_UUID.toString();

        var req = new AddTeamMemberRequest();
        req.setEmail("user@example.com");
        req.setRole(Role.PLAYER);

        var teamEntity = new TeamEntity();
        teamEntity.setId(TEAM_UUID);
        when(teamRepository.findById(TEAM_UUID)).thenReturn(Optional.of(teamEntity));

        var userEntity = new UserEntity();
        userEntity.setId(USER_UUID);
        userEntity.setEmail("user@example.com");
        userEntity.setUsername("testuser");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(userEntity));

        // Mock that user is not already a member
        when(teamMemberRepository.findByTeamIdAndUserId(TEAM_UUID, USER_UUID))
                .thenReturn(Optional.empty());

        var tmEntity = new TeamMemberEntity();
        tmEntity.setId(MEMBER_UUID);
        tmEntity.setTeamId(TEAM_UUID);
        tmEntity.setUserId(USER_UUID);
        tmEntity.setRole(Role.PLAYER);
        when(teamMemberRepository.save(any(TeamMemberEntity.class))).thenReturn(tmEntity);

        // when / then
        mockMvc.perform(post("/teams/{teamId}/members", teamId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teamMemberId").value(MEMBER_UUID.toString()))
                .andExpect(jsonPath("$.teamId").value(teamId))
                .andExpect(jsonPath("$.userId").value(USER_UUID.toString()))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("user@example.com"))
                .andExpect(jsonPath("$.role").value("PLAYER"));
    }

    @Test
    void testAddTeamMemberEndpoint_duplicateMember() throws Exception {
        String teamId = TEAM_UUID.toString();

        var req = new AddTeamMemberRequest();
        req.setEmail("user@example.com");
        req.setRole(Role.PLAYER);

        var teamEntity = new TeamEntity();
        teamEntity.setId(TEAM_UUID);
        when(teamRepository.findById(TEAM_UUID)).thenReturn(Optional.of(teamEntity));

        var userEntity = new UserEntity();
        userEntity.setId(USER_UUID);
        userEntity.setEmail("user@example.com");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(userEntity));

        // Mock that user is already a member
        var existingMember = new TeamMemberEntity();
        when(teamMemberRepository.findByTeamIdAndUserId(TEAM_UUID, USER_UUID))
                .thenReturn(Optional.of(existingMember));

        // when / then
        mockMvc.perform(post("/teams/{teamId}/members", teamId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("User is already a member of this team"));
    }

    @Test
    void testGetTeamsByOwnerEndpoint() throws Exception {
        String ownerId = OWNER_UUID.toString();

        var entity = new TeamEntity();
        entity.setId(TEAM_UUID);
        entity.setTeamName("Alpha");
        entity.setGameName("LOL");
        entity.setOwnerId(OWNER_UUID);

        when(teamRepository.findByOwnerId(OWNER_UUID))
                .thenReturn(List.of(entity));

        // when / then
        mockMvc.perform(get("/teams/owner/{ownerId}", ownerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].teamId").value(TEAM_UUID.toString()))
                .andExpect(jsonPath("$[0].teamName").value("Alpha"))
                .andExpect(jsonPath("$[0].gameName").value("LOL"))
                .andExpect(jsonPath("$[0].ownerId").value(ownerId));
    }

    @Test
    void testGetTeamMembersByTeamEndpoint() throws Exception {
        String teamId = TEAM_UUID.toString();

        var tm = new TeamMemberEntity();
        tm.setId(MEMBER_UUID);
        tm.setTeamId(TEAM_UUID);
        tm.setUserId(USER_UUID);
        tm.setRole(Role.PLAYER);

        var user = new UserEntity();
        user.setId(USER_UUID);
        user.setUsername("testuser");
        user.setEmail("test@example.com");

        when(teamMemberRepository.findByTeamId(TEAM_UUID)).thenReturn(List.of(tm));
        when(userRepository.findById(USER_UUID)).thenReturn(Optional.of(user));

        // when / then
        mockMvc.perform(get("/teams/{teamId}/members", teamId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.members[0].teamMemberId").value(MEMBER_UUID.toString()))
                .andExpect(jsonPath("$.members[0].teamId").value(teamId))
                .andExpect(jsonPath("$.members[0].userId").value(USER_UUID.toString()))
                .andExpect(jsonPath("$.members[0].username").value("testuser"))
                .andExpect(jsonPath("$.members[0].email").value("test@example.com"))
                .andExpect(jsonPath("$.members[0].role").value("PLAYER"))
                .andExpect(jsonPath("$.totalCount").value(1));
    }


    @Test
    void testRemoveTeamMemberEndpoint() throws Exception {
        // given
        String teamId = TEAM_UUID.toString();
        String userId = USER_UUID.toString();
        String ownerId = OWNER_UUID.toString();

        var team = new TeamEntity();
        team.setId(TEAM_UUID);
        team.setOwnerId(OWNER_UUID);
        when(teamRepository.findById(TEAM_UUID)).thenReturn(Optional.of(team));

        var teamMember = new TeamMemberEntity();
        teamMember.setId(MEMBER_UUID);
        teamMember.setTeamId(TEAM_UUID);
        teamMember.setUserId(USER_UUID);
        teamMember.setRole(Role.PLAYER);
        when(teamMemberRepository.findByTeamIdAndUserId(TEAM_UUID, USER_UUID))
                .thenReturn(Optional.of(teamMember));

        RemoveTeamMemberResponse expectedResponse = new RemoveTeamMemberResponse(
                MEMBER_UUID.toString(), teamId, userId, Role.PLAYER, true);

        when(removeTeamMemberUseCase.removeTeamMember(any(RemoveTeamMemberRequest.class)))
                .thenReturn(expectedResponse);

        // when / then
        mockMvc.perform(delete("/teams/{teamId}/members/{userId}", teamId, userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teamMemberId").value(MEMBER_UUID.toString()))
                .andExpect(jsonPath("$.teamId").value(teamId))
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.removed").value(true));
    }

    @Test
    void testGetTeamMembersByUserEndpoint() throws Exception {
        //Arange
        String userId = "user123";
        List< TeamResponse> teamResponses = Arrays.asList(
                new TeamResponse("team1", "Team Alpha", "Chess", "owner1"),
                new TeamResponse("team2", "Team Beta", "League of Legends", "owner2")
        );


        when(getTeamsByUserUseCase.getTeamsByUser(userId)).thenReturn(teamResponses);

        // Act & Assert
        mockMvc.perform(get("/teams/user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].teamId").value("team1"))
                .andExpect(jsonPath("$[0].teamName").value("Team Alpha"))
                .andExpect(jsonPath("$[0].gameName").value("Chess"))
                .andExpect(jsonPath("$[0].ownerId").value("owner1"))
                .andExpect(jsonPath("$[1].teamId").value("team2"))
                .andExpect(jsonPath("$[1].teamName").value("Team Beta"))
                .andExpect(jsonPath("$[1].gameName").value("League of Legends"))
                .andExpect(jsonPath("$[1].ownerId").value("owner2"));

    }

    @Test
    void testDeleteTeamEndpoint() throws Exception {
        //Arrange
        String teamId = TEAM_UUID.toString();

        DeleteTeamResponse expectedResponse = new DeleteTeamResponse(
                teamId,
                "Team Alpha",
                true,
                "Team successfully deleted"
        );

        when(deleteTeamUseCase.deleteTeam(any(DeleteTeamRequest.class)))
                .thenReturn(expectedResponse);

        // Act & Assert
        mockMvc.perform(delete("/teams/{teamId}", teamId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teamId").value(teamId))
                .andExpect(jsonPath("$.teamName").value("Team Alpha"))
                .andExpect(jsonPath("$.deleted").value(true))
                .andExpect(jsonPath("$.message").value("Team successfully deleted"));

        // Verify that the use case was called with the correct team ID
        ArgumentCaptor<DeleteTeamRequest> requestCaptor = ArgumentCaptor.forClass(DeleteTeamRequest.class);
        verify(deleteTeamUseCase).deleteTeam(requestCaptor.capture());
        assertThat(requestCaptor.getValue().getTeamId()).isEqualTo(teamId);
    }

    @Test
    void testDeleteTeamEndpoint_teamNotFound() throws Exception {
        // Arrange
        String nonExistentTeamId = NON_EXISTENT_TEAM_UUID.toString();

        when(deleteTeamUseCase.deleteTeam(any(DeleteTeamRequest.class)))
                .thenThrow(new IllegalArgumentException("Team not found"));

        // Act & Assert
        mockMvc.perform(delete("/teams/{teamId}", nonExistentTeamId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Team not found"))
                .andExpect(jsonPath("$.error").value("Bad Request"));
    }

    @Test
    void testDeleteTeamEndpoint_notTeamOwner() throws Exception {
        // Arrange
        String teamId = TEAM_UUID_2.toString();

        when(deleteTeamUseCase.deleteTeam(any(DeleteTeamRequest.class)))
                .thenThrow(new AccessDeniedException("Only the team owner can delete the team"));

        // Act & Assert
        mockMvc.perform(delete("/teams/{teamId}", teamId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Only the team owner can delete the team"))
                .andExpect(jsonPath("$.error").value("Forbidden"));
    }
}