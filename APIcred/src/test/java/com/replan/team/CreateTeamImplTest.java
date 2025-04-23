package com.replan.team;

import com.replan.business.impl.team.CreateTeamImpl;
import com.replan.domain.requests.CreateTeamRequest;
import com.replan.domain.responses.CreateTeamResponse;
import com.replan.persistance.TeamRepository;

import com.replan.persistance.entity.TeamEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


import static org.assertj.core.api.Assertions.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.argThat;


public class CreateTeamImplTest {
    @Mock
    private TeamRepository teamRepository;
    @InjectMocks
    private CreateTeamImpl subject;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void happyPath_shouldSaveAndReturnResponse() {
        // Assign
        var req = new CreateTeamRequest("Alpha", "Chess", "owner123");
        var saved = new TeamEntity();
        saved.setId("team-uuid");
        saved.setTeamName("Alpha");
        saved.setGameName("Chess");
        saved.setOwnerId("owner123");
        when(teamRepository.save(any(TeamEntity.class))).thenReturn(saved);

        // Act
        CreateTeamResponse resp = subject.createTeam(req);

        // Assert
        assertThat(resp.getTeamId()).isEqualTo("team-uuid");
        assertThat(resp.getTeamName()).isEqualTo("Alpha");
        assertThat(resp.getGameName()).isEqualTo("Chess");
        assertThat(resp.getOwnerId()).isEqualTo("owner123");
        verify(teamRepository).save(argThat(te ->
                te.getTeamName().equals("Alpha") &&
                        te.getGameName().equals("Chess") &&
                        te.getOwnerId().equals("owner123")
        ));
    }

    @Test
    void missingTeamName_shouldThrow() {
        var req = new CreateTeamRequest("", "Chess", "owner123");
        assertThatThrownBy(() -> subject.createTeam(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Team name cannot be empty");
    }

    @Test
    void missingGameName_shouldThrow() {
        var req = new CreateTeamRequest("Alpha", null, "owner123");
        assertThatThrownBy(() -> subject.createTeam(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Game name cannot be empty");
    }

    @Test
    void missingOwnerId_shouldThrow() {
        var req = new CreateTeamRequest("Alpha", "Chess", "");
        assertThatThrownBy(() -> subject.createTeam(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Owner id cannot be empty");
    }

}
