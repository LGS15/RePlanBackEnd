package com.replan.team;

import com.replan.business.impl.team.CreateTeamImpl;
import com.replan.domain.objects.Role;
import com.replan.domain.requests.CreateTeamRequest;
import com.replan.domain.responses.CreateTeamResponse;
import com.replan.persistance.TeamMemberRepository;
import com.replan.persistance.TeamRepository;

import com.replan.persistance.UserRepository;
import com.replan.persistance.entity.TeamEntity;
import com.replan.persistance.entity.TeamMemberEntity;
import com.replan.persistance.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


import java.util.Optional;
import java.util.UUID;

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
    @Mock
    private TeamMemberRepository teamMemberRepository;
    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void happyPath_shouldSaveAndReturnResponse() {
        var req = new CreateTeamRequest("Alpha", "Chess", "owner123");

        // Mock the user repository to return the owner
        var userEntity = new UserEntity();
        userEntity.setId("owner123");
        when(userRepository.findById(UUID.fromString("owner123"))).thenReturn(Optional.of(userEntity));

        // Mock the team repository
        var savedTeam = new TeamEntity();
        savedTeam.setId("team-uuid");
        savedTeam.setTeamName("Alpha");
        savedTeam.setGameName("Chess");
        savedTeam.setOwnerId("owner123");
        when(teamRepository.save(any(TeamEntity.class))).thenReturn(savedTeam);

        // Mock the team member repository
        var savedMember = new TeamMemberEntity();
        savedMember.setId("member-uuid");
        savedMember.setTeamId("team-uuid");
        savedMember.setUserId("owner123");
        savedMember.setRole(Role.OWNER);
        when(teamMemberRepository.save(any(TeamMemberEntity.class))).thenReturn(savedMember);

        // Act
        CreateTeamResponse resp = subject.createTeam(req);

        // Assert
        assertThat(resp.getTeamId()).isEqualTo("team-uuid");
        assertThat(resp.getTeamName()).isEqualTo("Alpha");
        assertThat(resp.getGameName()).isEqualTo("Chess");
        assertThat(resp.getOwnerId()).isEqualTo("owner123");

        // Verify team was saved
        verify(teamRepository).save(argThat(te ->
                te.getTeamName().equals("Alpha") &&
                        te.getGameName().equals("Chess") &&
                        te.getOwnerId().equals("owner123")
        ));

        // Verify team member was created with OWNER role
        ArgumentCaptor<TeamMemberEntity> memberCaptor = ArgumentCaptor.forClass(TeamMemberEntity.class);
        verify(teamMemberRepository).save(memberCaptor.capture());

        TeamMemberEntity capturedMember = memberCaptor.getValue();
        assertThat(capturedMember.getTeamId()).isEqualTo("team-uuid");
        assertThat(capturedMember.getUserId()).isEqualTo("owner123");
        assertThat(capturedMember.getRole()).isEqualTo(Role.OWNER);
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

    @Test
    void nonExistentOwner_shouldThrow() {
        var req = new CreateTeamRequest("Alpha", "Chess", "nonexistent");
        when(userRepository.findById(UUID.fromString("nonexistent"))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> subject.createTeam(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Owner does not exist");
    }

}
