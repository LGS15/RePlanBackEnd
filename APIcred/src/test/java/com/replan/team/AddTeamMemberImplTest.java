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

import com.replan.persistance.UserRepository;
import com.replan.persistance.entity.TeamEntity;
import com.replan.persistance.entity.TeamMemberEntity;
import com.replan.persistance.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


class AddTeamMemberImplTest {

    @Mock
    private TeamRepository teamRepository;
    @Mock
    private TeamMemberRepository tmRepo;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private AddTeamMemberImpl subject;

    private static final UUID TEAM_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID MEMBER_ID = UUID.randomUUID();
    private static final UUID NONEXISTENT_TEAM_ID = UUID.randomUUID();

    @BeforeEach void setUp() { MockitoAnnotations.openMocks(this); }

    @Test
    void happyPath_shouldSaveAndReturnResponse() {
        // Arrange
        var teamEnt = new TeamEntity();
        teamEnt.setId(TEAM_ID);
        when(teamRepository.findById(TEAM_ID)).thenReturn(Optional.of(teamEnt));

        // User exists
        var userEnt = new UserEntity();
        userEnt.setId(USER_ID);
        userEnt.setEmail("user@example.com");
        userEnt.setUsername("testuser");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(userEnt));

        // User is not already a member
        when(tmRepo.findByTeamIdAndUserId(TEAM_ID, USER_ID)).thenReturn(Optional.empty());

        // Save in repo
        var me = new TeamMemberEntity();
        me.setId(MEMBER_ID);
        me.setTeamId(TEAM_ID);
        me.setUserId(USER_ID);
        me.setRole(Role.PLAYER);
        when(tmRepo.save(any(TeamMemberEntity.class))).thenReturn(me);

        // Request
        var req = new AddTeamMemberRequest();
        req.setTeamId(TEAM_ID.toString());
        req.setEmail("user@example.com");
        req.setRole(Role.PLAYER);

        // Act
        AddTeamMemberResponse resp = subject.addTeamMember(req);

        // Assert
        assertThat(resp.getTeamMemberId()).isEqualTo(MEMBER_ID.toString());
        assertThat(resp.getTeamId()).isEqualTo(TEAM_ID.toString());
        assertThat(resp.getUserId()).isEqualTo(USER_ID.toString());
        assertThat(resp.getUsername()).isEqualTo("testuser");
        assertThat(resp.getEmail()).isEqualTo("user@example.com");
        assertThat(resp.getRole()).isEqualTo(Role.PLAYER);

        verify(tmRepo).save(argThat(ent ->
                ent.getTeamId().equals(TEAM_ID)
                        && ent.getUserId().equals(USER_ID)
                        && ent.getRole().equals(Role.PLAYER)
        ));
    }

    @Test
    void missingTeam_shouldThrow() {
        // Arrange
        when(teamRepository.findById(NONEXISTENT_TEAM_ID)).thenReturn(Optional.empty());
        var req = new AddTeamMemberRequest();
        req.setTeamId(NONEXISTENT_TEAM_ID.toString());

        // Act & Assert
        assertThatThrownBy(() -> subject.addTeamMember(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Team not found");
    }

    @Test
    void missingUser_shouldThrow() {
        // Arrange
        var teamEnt = new TeamEntity();
        teamEnt.setId(TEAM_ID);
        when(teamRepository.findById(TEAM_ID)).thenReturn(Optional.of(teamEnt));

        // User does not exist
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        var req = new AddTeamMemberRequest();
        req.setTeamId(TEAM_ID.toString());
        req.setEmail("nonexistent@example.com");
        req.setRole(Role.PLAYER);

        // Act & Assert
        assertThatThrownBy(() -> subject.addTeamMember(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User with email nonexistent@example.com not found");
    }

    @Test
    void duplicateMember_shouldThrow() {
        // Arrange
        var teamEnt = new TeamEntity();
        teamEnt.setId(TEAM_ID);
        when(teamRepository.findById(TEAM_ID)).thenReturn(Optional.of(teamEnt));

        var userEnt = new UserEntity();
        userEnt.setId(USER_ID);
        userEnt.setEmail("user@example.com");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(userEnt));

        // User is already a member
        var existingMember = new TeamMemberEntity();
        existingMember.setTeamId(TEAM_ID);
        existingMember.setUserId(USER_ID);
        when(tmRepo.findByTeamIdAndUserId(TEAM_ID, USER_ID)).thenReturn(Optional.of(existingMember));

        var req = new AddTeamMemberRequest();
        req.setTeamId(TEAM_ID.toString());
        req.setEmail("user@example.com");
        req.setRole(Role.PLAYER);

        // Act & Assert
        assertThatThrownBy(() -> subject.addTeamMember(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User is already a member of this team");
    }

    @Test
    void databaseConstraintViolation_shouldThrow() {
        // Arrange
        var teamEnt = new TeamEntity();
        teamEnt.setId(TEAM_ID);
        when(teamRepository.findById(TEAM_ID)).thenReturn(Optional.of(teamEnt));

        var userEnt = new UserEntity();
        userEnt.setId(USER_ID);
        userEnt.setEmail("user@example.com");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(userEnt));

        when(tmRepo.findByTeamIdAndUserId(TEAM_ID, USER_ID)).thenReturn(Optional.empty());
        when(tmRepo.save(any(TeamMemberEntity.class))).thenThrow(new DataIntegrityViolationException("Duplicate key"));

        var req = new AddTeamMemberRequest();
        req.setTeamId(TEAM_ID.toString());
        req.setEmail("user@example.com");
        req.setRole(Role.PLAYER);

        // Act & Assert
        assertThatThrownBy(() -> subject.addTeamMember(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User is already a member of this team");
    }
}
