package com.replan.team;

import com.replan.business.impl.team.GetTeamsByUserImpl;
import com.replan.domain.responses.TeamResponse;
import com.replan.persistance.TeamMemberRepository;
import com.replan.persistance.TeamRepository;
import com.replan.persistance.entity.TeamEntity;
import com.replan.persistance.entity.TeamMemberEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.when;

class  GetTeamsByUserImplTest {

    @Mock
    private TeamMemberRepository teamMemberRepository;
    @Mock
    private TeamRepository teamRepository;
    @InjectMocks
    private GetTeamsByUserImpl subject;

    @BeforeEach
    void setUp() { MockitoAnnotations.openMocks(this); }

    @Test
    void shouldReturnTeamsWhereUserIsMember() {
        // Arrange
        String userId = "user123";


        TeamMemberEntity member1 = new TeamMemberEntity();
        member1.setId("tm1");
        member1.setTeamId("team1");
        member1.setUserId(userId);

        TeamMemberEntity member2 = new TeamMemberEntity();
        member2.setId("tm2");
        member2.setTeamId("team2");
        member2.setUserId(userId);


        TeamEntity team1 = new TeamEntity();
        team1.setId("team1");
        team1.setTeamName("Team One");
        team1.setGameName("Game One");
        team1.setOwnerId("owner1");

        TeamEntity team2 = new TeamEntity();
        team2.setId("team2");
        team2.setTeamName("Team Two");
        team2.setGameName("Game Two");
        team2.setOwnerId("owner2");

        when(teamMemberRepository.findByUserId(userId)).thenReturn(Arrays.asList(member1, member2));
        when(teamRepository.findAllById(Arrays.asList("team1", "team2"))).thenReturn(Arrays.asList(team1, team2));

        //Act
        List<TeamResponse> result = subject.getTeamsByUser(userId);

        //Assert
        assertThat(result).hasSize(2);
        assertThat(result).extracting("teamId").containsExactlyInAnyOrder("team1", "team2");
        assertThat(result).extracting("teamName").containsExactlyInAnyOrder("Team One", "Team Two");
        assertThat(result).extracting("gameName").containsExactlyInAnyOrder("Game One", "Game Two");
        assertThat(result).extracting("ownerId").containsExactlyInAnyOrder("owner1", "owner2");
    }

    @Test
    void shouldReturnEmptyListWhenUserIsNotMemberOfAnyTeam() {
        // Arrange
        String userId = "user456";
        when(teamMemberRepository.findByUserId(userId)).thenReturn(List.of());

        // Act
        List<TeamResponse> result = subject.getTeamsByUser(userId);

        // Assert
        assertThat(result).isEmpty();
    }

}
