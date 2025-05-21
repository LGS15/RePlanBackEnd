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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.when;

class  GetTeamsByUserImplTest {

    @Mock
    private TeamMemberRepository teamMemberRepository;
    @Mock
    private TeamRepository teamRepository;
    @InjectMocks
    private GetTeamsByUserImpl subject;

    private static final UUID USER_UUID = UUID.randomUUID();
    private static final UUID NONEXISTENT_USER_UUID = UUID.randomUUID();
    private static final UUID TEAM_MEMBER_1_UUID = UUID.randomUUID();
    private static final UUID TEAM_MEMBER_2_UUID = UUID.randomUUID();
    private static final UUID TEAM_1_UUID = UUID.randomUUID();
    private static final UUID TEAM_2_UUID = UUID.randomUUID();
    private static final UUID OWNER_1_UUID = UUID.randomUUID();
    private static final UUID OWNER_2_UUID = UUID.randomUUID();

    @BeforeEach
    void setUp() { MockitoAnnotations.openMocks(this); }

    @Test
    void shouldReturnTeamsWhereUserIsMember() {
        // Arrange
        String userId = USER_UUID.toString();

        TeamMemberEntity member1 = new TeamMemberEntity();
        member1.setId(TEAM_MEMBER_1_UUID);
        member1.setTeamId(TEAM_1_UUID);
        member1.setUserId(USER_UUID);

        TeamMemberEntity member2 = new TeamMemberEntity();
        member2.setId(TEAM_MEMBER_2_UUID);
        member2.setTeamId(TEAM_2_UUID);
        member2.setUserId(USER_UUID);

        TeamEntity team1 = new TeamEntity();
        team1.setId(TEAM_1_UUID);
        team1.setTeamName("Team One");
        team1.setGameName("Game One");
        team1.setOwnerId(OWNER_1_UUID);

        TeamEntity team2 = new TeamEntity();
        team2.setId(TEAM_2_UUID);
        team2.setTeamName("Team Two");
        team2.setGameName("Game Two");
        team2.setOwnerId(OWNER_2_UUID);

        when(teamMemberRepository.findByUserId(USER_UUID))
                .thenReturn(Arrays.asList(member1, member2));

        when(teamRepository.findAllById(Arrays.asList(TEAM_1_UUID, TEAM_2_UUID)))
                .thenReturn(Arrays.asList(team1, team2));

        // Act
        List<TeamResponse> result = subject.getTeamsByUser(userId);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).extracting("teamId")
                .containsExactlyInAnyOrder(TEAM_1_UUID.toString(), TEAM_2_UUID.toString());
        assertThat(result).extracting("teamName")
                .containsExactlyInAnyOrder("Team One", "Team Two");
        assertThat(result).extracting("gameName")
                .containsExactlyInAnyOrder("Game One", "Game Two");
        assertThat(result).extracting("ownerId")
                .containsExactlyInAnyOrder(OWNER_1_UUID.toString(), OWNER_2_UUID.toString());
    }

    @Test
    void shouldReturnEmptyListWhenUserIsNotMemberOfAnyTeam() {
        // Arrange
        String userId = NONEXISTENT_USER_UUID.toString();
        when(teamMemberRepository.findByUserId(NONEXISTENT_USER_UUID)).thenReturn(List.of());

        // Act
        List<TeamResponse> result = subject.getTeamsByUser(userId);

        // Assert
        assertThat(result).isEmpty();
    }

}
