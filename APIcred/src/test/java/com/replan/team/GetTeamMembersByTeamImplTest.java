package com.replan.team;

import com.replan.business.impl.teamMember.GetTeamMembersByTeamImpl;
import com.replan.domain.objects.Role;
import com.replan.domain.requests.GetTeamMembersByTeamRequest;
import com.replan.domain.responses.GetTeamMembersByTeamResponse;
import com.replan.persistance.TeamMemberRepository;
import com.replan.persistance.UserRepository;
import com.replan.persistance.entity.TeamMemberEntity;
import com.replan.persistance.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class GetTeamMembersByTeamImplTest {

    @Mock
    private TeamMemberRepository tmRepo;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private GetTeamMembersByTeamImpl subject;

    private static final UUID TEAM_ID = UUID.randomUUID();
    private static final UUID MEMBER_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID NONEXISTENT_TEAM_ID = UUID.randomUUID();

    @BeforeEach
    void setUp() { MockitoAnnotations.openMocks(this); }

    @Test
    void whenFound_shouldMapAndCount() {
        // Arrange
        var teamMemberEntity = new TeamMemberEntity();
        teamMemberEntity.setId(MEMBER_ID);
        teamMemberEntity.setTeamId(TEAM_ID);
        teamMemberEntity.setUserId(USER_ID);
        teamMemberEntity.setRole(Role.COACH);

        var userEntity = new UserEntity();
        userEntity.setId(USER_ID);
        userEntity.setUsername("testuser");
        userEntity.setEmail("test@example.com");

        when(tmRepo.findByTeamId(TEAM_ID)).thenReturn(List.of(teamMemberEntity));
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(userEntity));

        var req = new GetTeamMembersByTeamRequest(TEAM_ID.toString());

        // Act
        GetTeamMembersByTeamResponse resp = subject.getTeamMembers(req);

        // Assert
        assertThat(resp.getMembers()).hasSize(1);
        var member = resp.getMembers().get(0);
        assertThat(member.getTeamMemberId()).isEqualTo(MEMBER_ID.toString());
        assertThat(member.getUserId()).isEqualTo(USER_ID.toString());
        assertThat(member.getUsername()).isEqualTo("testuser");
        assertThat(member.getEmail()).isEqualTo("test@example.com");
        assertThat(member.getRole()).isEqualTo(Role.COACH);
        assertThat(resp.getTotalCount()).isEqualTo(1);
    }

    @Test
    void whenFoundButUserNotFound_shouldMapWithNullUserInfo() {
        // Arrange
        var teamMemberEntity = new TeamMemberEntity();
        teamMemberEntity.setId(MEMBER_ID);
        teamMemberEntity.setTeamId(TEAM_ID);
        teamMemberEntity.setUserId(USER_ID);
        teamMemberEntity.setRole(Role.PLAYER);

        when(tmRepo.findByTeamId(TEAM_ID)).thenReturn(List.of(teamMemberEntity));
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        var req = new GetTeamMembersByTeamRequest(TEAM_ID.toString());

        // Act
        GetTeamMembersByTeamResponse resp = subject.getTeamMembers(req);

        // Assert
        assertThat(resp.getMembers()).hasSize(1);
        var member = resp.getMembers().get(0);
        assertThat(member.getTeamMemberId()).isEqualTo(MEMBER_ID.toString());
        assertThat(member.getUserId()).isEqualTo(USER_ID.toString());
        assertThat(member.getUsername()).isNull();
        assertThat(member.getEmail()).isNull();
        assertThat(member.getRole()).isEqualTo(Role.PLAYER);
        assertThat(resp.getTotalCount()).isEqualTo(1);
    }

    @Test
    void whenNoneFound_shouldBeEmpty() {
        // Arrange
        when(tmRepo.findByTeamId(NONEXISTENT_TEAM_ID)).thenReturn(List.of());

        var req = new GetTeamMembersByTeamRequest(NONEXISTENT_TEAM_ID.toString());

        // Act
        GetTeamMembersByTeamResponse resp = subject.getTeamMembers(req);

        // Assert
        assertThat(resp.getMembers()).isEmpty();
        assertThat(resp.getTotalCount()).isZero();
    }
}
