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

    private static final UUID TEAM_UUID = UUID.randomUUID();
    private static final UUID MEMBER_UUID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void happyPath_shouldSaveAndReturnResponse() {
        UUID ownerUUID = UUID.randomUUID();
        String ownerId = ownerUUID.toString();
        var req = new CreateTeamRequest("Alpha", "Chess", ownerId);

        // Mock the user repository to return the owner
        var userEntity = new UserEntity();
        userEntity.setId(ownerUUID);
        when(userRepository.findById(ownerUUID)).thenReturn(Optional.of(userEntity));

        // Mock the team repository
        var savedTeam = new TeamEntity();
        savedTeam.setId(TEAM_UUID);
        savedTeam.setTeamName("Alpha");
        savedTeam.setGameName("Chess");
        savedTeam.setOwnerId(ownerUUID);
        when(teamRepository.save(any(TeamEntity.class))).thenReturn(savedTeam);

        // Mock the team member repository
        var savedMember = new TeamMemberEntity();
        savedMember.setId(MEMBER_UUID);
        savedMember.setTeamId(TEAM_UUID);
        savedMember.setUserId(ownerUUID);
        savedMember.setRole(Role.OWNER);
        when(teamMemberRepository.save(any(TeamMemberEntity.class))).thenReturn(savedMember);

        // Act
        CreateTeamResponse resp = subject.createTeam(req);

        // Assert
        assertThat(resp.getTeamId()).isEqualTo(TEAM_UUID.toString());
        assertThat(resp.getTeamName()).isEqualTo("Alpha");
        assertThat(resp.getGameName()).isEqualTo("Chess");
        assertThat(resp.getOwnerId()).isEqualTo(ownerId);

        // Verify team was saved
        verify(teamRepository).save(argThat(te ->
                te.getTeamName().equals("Alpha") &&
                        te.getGameName().equals("Chess") &&
                        te.getOwnerId().equals(ownerUUID)
        ));

        // Verify team member was created with OWNER role
        ArgumentCaptor<TeamMemberEntity> memberCaptor = ArgumentCaptor.forClass(TeamMemberEntity.class);
        verify(teamMemberRepository).save(memberCaptor.capture());

        TeamMemberEntity capturedMember = memberCaptor.getValue();
        assertThat(capturedMember.getTeamId()).isEqualTo(TEAM_UUID);
        assertThat(capturedMember.getUserId()).isEqualTo(ownerUUID);
        assertThat(capturedMember.getRole()).isEqualTo(Role.OWNER);
    }

    @Test
    void missingTeamName_shouldThrow() {
        // Generate a valid UUID for owner
        String ownerId = UUID.randomUUID().toString();
        var req = new CreateTeamRequest("", "Chess", ownerId);

        assertThatThrownBy(() -> subject.createTeam(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Team name cannot be empty");
    }

    @Test
    void missingGameName_shouldThrow() {
        // Generate a valid UUID for owner
        String ownerId = UUID.randomUUID().toString();
        var req = new CreateTeamRequest("Alpha", null, ownerId);

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
        // Generate a valid UUID for a non-existent owner
        UUID nonExistentUUID = UUID.randomUUID();
        String nonExistentId = nonExistentUUID.toString();
        var req = new CreateTeamRequest("Alpha", "Chess", nonExistentId);
        when(userRepository.findById(nonExistentUUID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> subject.createTeam(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Owner does not exist");
    }
}