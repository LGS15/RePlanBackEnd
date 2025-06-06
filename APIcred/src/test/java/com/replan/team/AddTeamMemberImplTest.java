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

        var teamEnt = new TeamEntity();
        teamEnt.setId(TEAM_ID);
        when(teamRepository.findById(TEAM_ID)).thenReturn(Optional.of(teamEnt));

        // user exists
        var userEnt = new UserEntity();
        userEnt.setId(USER_ID);
        userEnt.setEmail("user@example.com");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(userEnt));

        // save in repo
        var me = new TeamMemberEntity();
        me.setId(MEMBER_ID);
        me.setTeamId(TEAM_ID);
        me.setUserId(USER_ID);
        me.setRole(Role.valueOf("PLAYER"));
        when(tmRepo.save(any(TeamMemberEntity.class))).thenReturn(me);

        // request
        var req = new AddTeamMemberRequest();
        req.setTeamId(TEAM_ID.toString());
        req.setEmail("user@example.com");
        req.setRole(Role.PLAYER);

        AddTeamMemberResponse resp = subject.addTeamMember(req);

        assertThat(resp.getTeamMemberId()).isEqualTo(MEMBER_ID.toString());
        assertThat(resp.getTeamId()).isEqualTo(TEAM_ID.toString());
        assertThat(resp.getUserId()).isEqualTo(USER_ID.toString());
        assertThat(resp.getRole()).isEqualTo(Role.PLAYER);

        verify(tmRepo).save(argThat(ent ->
                ent.getTeamId().equals(TEAM_ID)
                        && ent.getUserId().equals(USER_ID)
                        && ent.getRole().equals(Role.valueOf("PLAYER"))
        ));
    }

    @Test
    void missingTeam_shouldThrow() {
        when(teamRepository.findById(NONEXISTENT_TEAM_ID)).thenReturn(Optional.empty());
        var req = new AddTeamMemberRequest();
        req.setTeamId(NONEXISTENT_TEAM_ID.toString());
        assertThatThrownBy(() -> subject.addTeamMember(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Team not found");
    }

    @Test
    void missingUser_shouldThrow() {

        var teamEnt = new TeamEntity();
        teamEnt.setId(TEAM_ID);
        when(teamRepository.findById(TEAM_ID)).thenReturn(Optional.of(teamEnt));

        // user does not exist
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        var req = new AddTeamMemberRequest();
        req.setTeamId(TEAM_ID.toString());
        req.setEmail("nonexistent@example.com");
        req.setRole(Role.PLAYER);

        assertThatThrownBy(() -> subject.addTeamMember(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User with email nonexistent@example.com not found");
    }
}
