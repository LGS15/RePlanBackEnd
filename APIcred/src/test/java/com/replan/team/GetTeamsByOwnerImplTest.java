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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

class GetTeamsByOwnerImplTest {

    @Mock
    private TeamRepository teamRepository;
    @InjectMocks
    private GetTeamsByOwnerImpl subject;

    @BeforeEach
    void setUp() { MockitoAnnotations.openMocks(this); }

    @Test
    void whenFound_shouldMapAll() {
        var e1 = new TeamEntity(); e1.setId("t1"); e1.setTeamName("One"); e1.setGameName("G1"); e1.setOwnerId("o1");
        var e2 = new TeamEntity(); e2.setId("t2"); e2.setTeamName("Two"); e2.setGameName("G2"); e2.setOwnerId("o1");
        when(teamRepository.findByOwnerId("o1")).thenReturn(List.of(e1, e2));

        List<TeamResponse> out = subject.getTeamsByOwner("o1");

        assertThat(out)
                .extracting(TeamResponse::getTeamId, TeamResponse::getTeamName, TeamResponse::getGameName, TeamResponse::getOwnerId)
                .containsExactly(
                        tuple("t1","One","G1","o1"),
                        tuple("t2","Two","G2","o1")
                );
    }

    @Test
    void whenNoneFound_shouldReturnEmpty() {
        when(teamRepository.findByOwnerId("nx")).thenReturn(List.of());
        assertThat(subject.getTeamsByOwner("nx")).isEmpty();
    }
}