package com.replan.team;

import com.replan.business.impl.teamMember.AddTeamMemberImpl;
import com.replan.business.impl.team.CreateTeamImpl;
import com.replan.business.usecases.team.CreateTeamUseCase;
import com.replan.business.usecases.teamMember.AddTeamMemberUseCase;
import com.replan.domain.objects.Role;
import com.replan.domain.requests.AddTeamMemberRequest;
import com.replan.domain.requests.CreateTeamRequest;
import com.replan.domain.responses.AddTeamMemberResponse;
import com.replan.domain.responses.CreateTeamResponse;
import com.replan.persistance.TeamMemberRepository;
import com.replan.persistance.TeamRepository;
import com.replan.persistance.team.InMemoryTeamMemberRepository;
import com.replan.persistance.team.InMemoryTeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AddTeamMemberImplTest {
    private TeamRepository teamRepository;
    private TeamMemberRepository teamMemberRepository;
    private AddTeamMemberUseCase addTeamMemberUseCase;
    private CreateTeamUseCase createTeamUseCase;

    @BeforeEach
    public void setUp() {
        teamRepository = new InMemoryTeamRepository();
        teamMemberRepository = new InMemoryTeamMemberRepository();
        addTeamMemberUseCase = new AddTeamMemberImpl(teamRepository, teamMemberRepository);
        createTeamUseCase = new CreateTeamImpl(teamRepository);
    }

    @Test
    public void testAddTeamMemberSuccess() {
        // Create a team
        CreateTeamRequest teamRequest = new CreateTeamRequest("Alpha Squad", "League of Legends", "owner123");
        CreateTeamResponse teamResponse = createTeamUseCase.createTeam(teamRequest);

        // Prepare request
        AddTeamMemberRequest addRequest = new AddTeamMemberRequest();
        addRequest.setTeamId(teamResponse.getTeamId());
        addRequest.setUserId("user456");
        addRequest.setRole(Role.PLAYER);

        // Adding the team member
        AddTeamMemberResponse addResponse = addTeamMemberUseCase.addTeamMember(addRequest);

        // Verify outcome
        assertNotNull(addResponse.getTeamMemberId());
        assertEquals(teamResponse.getTeamId(), addResponse.getTeamId());
        assertEquals("user456", addResponse.getUserId());
        assertEquals(Role.PLAYER, addResponse.getRole());
    }

    @Test
    public void testAddTeamMemberTeamNotFound() {
        // Give a request with a non-existent teamId
        AddTeamMemberRequest addRequest = new AddTeamMemberRequest();
        addRequest.setTeamId("nonexistentTeamId");
        addRequest.setUserId("user456");
        addRequest.setRole(Role.PLAYER);

        // Exception because the team is not found/doesn't exist
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            addTeamMemberUseCase.addTeamMember(addRequest);
        });
        assertEquals("Team not found", exception.getMessage());
    }
}
