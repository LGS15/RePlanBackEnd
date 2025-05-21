package com.replan.team;

import com.replan.business.impl.teamMember.RemoveTeamMemberImpl;
import com.replan.business.usecases.teamMember.RemoveTeamMemberUseCase;
import com.replan.domain.objects.Role;
import com.replan.domain.requests.RemoveTeamMemberRequest;
import com.replan.domain.responses.RemoveTeamMemberResponse;
import com.replan.persistance.TeamMemberRepository;
import com.replan.persistance.TeamRepository;
import com.replan.persistance.entity.TeamEntity;
import com.replan.persistance.entity.TeamMemberEntity;
import com.replan.persistance.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class RemoveTeamMemberImplTest {
    @Mock
    private TeamMemberRepository teamMemberRepository;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private RemoveTeamMemberImpl subject;

    private static final UUID TEAM_ID = UUID.randomUUID();
    private static final UUID OWNER_ID = UUID.randomUUID();
    private static final UUID MEMBER_ID = UUID.randomUUID();
    private static final UUID MEMBER_USER_ID = UUID.randomUUID();
    private static final UUID OTHER_USER_ID = UUID.randomUUID();
    private static final UUID NON_EXISTENT_TEAM_ID = UUID.randomUUID();
    private static final UUID NON_EXISTENT_USER_ID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void ownerRemovingMember_shouldSucceed() {
        // Arrange
        TeamEntity team = new TeamEntity();
        team.setId(TEAM_ID);
        team.setOwnerId(OWNER_ID);
        when(teamRepository.findById(TEAM_ID)).thenReturn(Optional.of(team));

        // Mock team member lookup
        TeamMemberEntity teamMember = new TeamMemberEntity();
        teamMember.setId(MEMBER_ID);
        teamMember.setTeamId(TEAM_ID);
        teamMember.setUserId(MEMBER_USER_ID);
        teamMember.setRole(Role.PLAYER);
        when(teamMemberRepository.findByTeamIdAndUserId(TEAM_ID, MEMBER_USER_ID))
                .thenReturn(Optional.of(teamMember));

        // Mock authentication context
        UserEntity ownerUser = new UserEntity();
        ownerUser.setId(OWNER_ID);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(ownerUser);

        // Create request
        RemoveTeamMemberRequest request = new RemoveTeamMemberRequest(TEAM_ID.toString(), MEMBER_USER_ID.toString());

        // Act
        RemoveTeamMemberResponse response = subject.removeTeamMember(request);

        // Assert
        assertThat(response.isRemoved()).isTrue();
        assertThat(UUID.fromString(response.getTeamMemberId())).isEqualTo(MEMBER_ID);
        assertThat(UUID.fromString(response.getTeamId())).isEqualTo(TEAM_ID);
        assertThat(UUID.fromString(response.getUserId())).isEqualTo(MEMBER_USER_ID);

        // Verify that member was deleted
        verify(teamMemberRepository).delete(teamMember);
    }

    @Test
    void memberRemovingSelf_shouldSucceed() {
        // Arrange
        TeamEntity team = new TeamEntity();
        team.setId(TEAM_ID);
        team.setOwnerId(OWNER_ID);
        when(teamRepository.findById(TEAM_ID)).thenReturn(Optional.of(team));


        TeamMemberEntity teamMember = new TeamMemberEntity();
        teamMember.setId(MEMBER_ID);
        teamMember.setTeamId(TEAM_ID);
        teamMember.setUserId(MEMBER_USER_ID);
        teamMember.setRole(Role.PLAYER);
        when(teamMemberRepository.findByTeamIdAndUserId(TEAM_ID, MEMBER_USER_ID))
                .thenReturn(Optional.of(teamMember));

        // Authentication context
        UserEntity memberUser = new UserEntity();
        memberUser.setId(MEMBER_USER_ID);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(memberUser);

        // Create request
        RemoveTeamMemberRequest request = new RemoveTeamMemberRequest(TEAM_ID.toString(), MEMBER_USER_ID.toString());

        // Act
        RemoveTeamMemberResponse response = subject.removeTeamMember(request);

        // Assert
        assertThat(response.isRemoved()).isTrue();
        verify(teamMemberRepository).delete(teamMember);
    }

    @Test
    void nonOwnerRemovingOtherMember_shouldThrow() {
        // Arrange
        TeamEntity team = new TeamEntity();
        team.setId(TEAM_ID);
        team.setOwnerId(OWNER_ID);
        when(teamRepository.findById(TEAM_ID)).thenReturn(Optional.of(team));

        TeamMemberEntity teamMember = new TeamMemberEntity();
        teamMember.setId(MEMBER_ID);
        teamMember.setTeamId(TEAM_ID);
        teamMember.setUserId(MEMBER_USER_ID);
        teamMember.setRole(Role.PLAYER);
        when(teamMemberRepository.findByTeamIdAndUserId(TEAM_ID, MEMBER_USER_ID))
                .thenReturn(Optional.of(teamMember));

        // Mock authentication context
        UserEntity otherUser = new UserEntity();
        otherUser.setId(OTHER_USER_ID);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(otherUser);

        // Create request
        RemoveTeamMemberRequest request = new RemoveTeamMemberRequest(TEAM_ID.toString(), MEMBER_USER_ID.toString());

        // Act & Assert
        assertThatThrownBy(() -> subject.removeTeamMember(request))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("Only the team owner can remove members");

        // Verify that no members were deleted
        verify(teamMemberRepository, never()).delete(any());
    }

    @Test
    void nonExistentTeam_shouldThrow() {
        // Arrange
        when(teamRepository.findById(NON_EXISTENT_TEAM_ID)).thenReturn(Optional.empty());

        // Create request
        RemoveTeamMemberRequest request = new RemoveTeamMemberRequest(NON_EXISTENT_TEAM_ID.toString(), MEMBER_USER_ID.toString());

        // Act & Assert
        assertThatThrownBy(() -> subject.removeTeamMember(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Team not found");
    }

    @Test
    void nonExistentTeamMember_shouldThrow() {
        // Arrange
        TeamEntity team = new TeamEntity();
        team.setId(TEAM_ID);
        team.setOwnerId(OWNER_ID);
        when(teamRepository.findById(TEAM_ID)).thenReturn(Optional.of(team));

        when(teamMemberRepository.findByTeamIdAndUserId(TEAM_ID, NON_EXISTENT_USER_ID))
                .thenReturn(Optional.empty());

        // Create request
        RemoveTeamMemberRequest request = new RemoveTeamMemberRequest(TEAM_ID.toString(), NON_EXISTENT_USER_ID.toString());

        // Act & Assert
        assertThatThrownBy(() -> subject.removeTeamMember(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Team Member not found");
    }

    @Test
    void notAuthenticated_shouldThrow() {
        // Arrange
        TeamEntity team = new TeamEntity();
        team.setId(TEAM_ID);
        team.setOwnerId(OWNER_ID);
        when(teamRepository.findById(TEAM_ID)).thenReturn(Optional.of(team));

        TeamMemberEntity teamMember = new TeamMemberEntity();
        teamMember.setId(MEMBER_ID);
        teamMember.setTeamId(TEAM_ID);
        teamMember.setUserId(MEMBER_USER_ID);
        when(teamMemberRepository.findByTeamIdAndUserId(TEAM_ID, MEMBER_USER_ID))
                .thenReturn(Optional.of(teamMember));

        // Mock authentication context to be null
        when(securityContext.getAuthentication()).thenReturn(null);

        // Create request
        RemoveTeamMemberRequest request = new RemoveTeamMemberRequest(TEAM_ID.toString(), MEMBER_USER_ID.toString());

        // Act & Assert
        assertThatThrownBy(() -> subject.removeTeamMember(request))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("User not authenticated");
    }
}
