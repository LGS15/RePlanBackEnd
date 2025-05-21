package com.replan.team;

import com.replan.business.impl.team.GetTeamsByOwnerImpl;
import com.replan.domain.responses.TeamResponse;
import com.replan.persistance.TeamRepository;
import com.replan.persistance.entity.TeamEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

class GetTeamsByOwnerImplTest {

    @Mock
    private TeamRepository teamRepository;
    @InjectMocks
    private GetTeamsByOwnerImpl subject;

    private static final UUID TEAM_ID_1 = UUID.randomUUID();
    private static final UUID TEAM_ID_2 = UUID.randomUUID();
    private static final UUID OWNER_ID = UUID.randomUUID();
    private static final UUID NONEXISTENT_OWNER_ID = UUID.randomUUID();


    @BeforeEach
    void setUp() { MockitoAnnotations.openMocks(this); }

    @Test
    void whenFound_shouldMapAll() {
        var e1 = new TeamEntity();
        e1.setId(TEAM_ID_1);
        e1.setTeamName("One");
        e1.setGameName("G1");
        e1.setOwnerId(OWNER_ID);

        var e2 = new TeamEntity();
        e2.setId(TEAM_ID_2);
        e2.setTeamName("Two");
        e2.setGameName("G2");
        e2.setOwnerId(OWNER_ID);

        when(teamRepository.findByOwnerId(OWNER_ID)).thenReturn(List.of(e1, e2));

        List<TeamResponse> out = subject.getTeamsByOwner(OWNER_ID.toString());

        assertThat(out)
                .extracting(TeamResponse::getTeamId, TeamResponse::getTeamName, TeamResponse::getGameName, TeamResponse::getOwnerId)
                .containsExactly(
                        tuple(TEAM_ID_1.toString(), "One", "G1", OWNER_ID.toString()),
                        tuple(TEAM_ID_2.toString(), "Two", "G2", OWNER_ID.toString())
                );
    }

    @Test
    void whenNoneFound_shouldReturnEmpty() {
        when(teamRepository.findByOwnerId(NONEXISTENT_OWNER_ID)).thenReturn(List.of());
        assertThat(subject.getTeamsByOwner(NONEXISTENT_OWNER_ID.toString())).isEmpty();
    }
}