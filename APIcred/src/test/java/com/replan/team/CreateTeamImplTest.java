package com.replan.team;

import com.replan.business.impl.team.CreateTeamImpl;
import com.replan.business.usecases.team.CreateTeamUseCase;
import com.replan.domain.objects.Team;
import com.replan.domain.requests.CreateTeamRequest;
import com.replan.domain.responses.CreateTeamResponse;
import com.replan.persistance.TeamRepository;
import com.replan.persistance.team.InMemoryTeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class CreateTeamImplTest {
    private TeamRepository teamRepository;
    private CreateTeamUseCase createTeamUseCase;

    @BeforeEach
    public void setUp() {
        teamRepository = new InMemoryTeamRepository();
        createTeamUseCase = new CreateTeamImpl(teamRepository);
    }

    @Test
    public void testCreateTeamSuccess() {
        // Create Request
        CreateTeamRequest request = new CreateTeamRequest("Alpha Squad", "League of Legends", "owner123");

        // Create Team
        CreateTeamResponse response = createTeamUseCase.createTeam(request);

        // Check Response
        assertNotNull(response.getTeamId());
        assertEquals("Alpha Squad", response.getTeamName());
        assertEquals("League of Legends", response.getGameName());
        assertEquals("owner123", response.getOwnerId());

        // Assert Final Outcome
        Team savedTeam = teamRepository.findById(response.getTeamId()).orElse(null);
        assertNotNull(savedTeam);
        assertEquals("Alpha Squad", savedTeam.getTeamName());
    }

    @Test
    public void testCreateTeamWithEmptyTeamNameThrowsException() {
        // Given an invalid request with an EMPTY team name
        CreateTeamRequest request = new CreateTeamRequest("", "League of Legends", "owner123");


        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            createTeamUseCase.createTeam(request);
        });
        assertEquals("Team name cannot be empty", exception.getMessage());
    }


}
