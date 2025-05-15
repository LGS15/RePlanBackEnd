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

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void ownerRemovingMember_shouldSucceed() {
        // Arrange
        String teamId = "team123";
        String ownerId = "owner123";
        String memberId = "member456";
        String memberUserId = "user456";

        // Mock team lookup
        TeamEntity team = new TeamEntity();
        team.setId(teamId);
        team.setOwnerId(ownerId);
        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));

        // Mock team member lookup
        TeamMemberEntity teamMember = new TeamMemberEntity();
        teamMember.setId(memberId);
        teamMember.setTeamId(teamId);
        teamMember.setUserId(memberUserId);
        teamMember.setRole(Role.PLAYER);
        when(teamMemberRepository.findByTeamIdAndUserId(teamId, memberUserId))
                .thenReturn(Optional.of(teamMember));

        // Mock authentication context
        UserEntity ownerUser = new UserEntity();
        ownerUser.setId(ownerId);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(ownerUser);

        // Create request
        RemoveTeamMemberRequest request = new RemoveTeamMemberRequest(teamId, memberUserId);

        // Act
        RemoveTeamMemberResponse response = subject.removeTeamMember(request);

        // Assert
        assertThat(response.isRemoved()).isTrue();
        assertThat(response.getTeamMemberId()).isEqualTo(memberId);
        assertThat(response.getTeamId()).isEqualTo(teamId);
        assertThat(response.getUserId()).isEqualTo(memberUserId);

        // Verify that member was deleted
        verify(teamMemberRepository).delete(teamMember);
    }

    @Test
    void memberRemovingSelf_shouldSucceed() {
        // Arrange
        String teamId = "team123";
        String ownerId = "owner123";
        String memberId = "member456";
        String memberUserId = "user456";

        // Mock team lookup
        TeamEntity team = new TeamEntity();
        team.setId(teamId);
        team.setOwnerId(ownerId);
        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));

        // Mock team member lookup
        TeamMemberEntity teamMember = new TeamMemberEntity();
        teamMember.setId(memberId);
        teamMember.setTeamId(teamId);
        teamMember.setUserId(memberUserId);
        teamMember.setRole(Role.PLAYER);
        when(teamMemberRepository.findByTeamIdAndUserId(teamId, memberUserId))
                .thenReturn(Optional.of(teamMember));

        // Mock authentication context
        UserEntity memberUser = new UserEntity();
        memberUser.setId(memberUserId);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(memberUser);

        // Create request
        RemoveTeamMemberRequest request = new RemoveTeamMemberRequest(teamId, memberUserId);

        // Act
        RemoveTeamMemberResponse response = subject.removeTeamMember(request);

        // Assert
        assertThat(response.isRemoved()).isTrue();
        verify(teamMemberRepository).delete(teamMember);
    }

    @Test
    void nonOwnerRemovingOtherMember_shouldThrow() {
        // Arrange
        String teamId = "team123";
        String ownerId = "owner123";
        String memberId = "member456";
        String memberUserId = "user456";
        String otherUserId = "user789"; // Non-owner trying to remove someone else

        // Mock team lookup
        TeamEntity team = new TeamEntity();
        team.setId(teamId);
        team.setOwnerId(ownerId);
        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));

        // Mock team member lookup
        TeamMemberEntity teamMember = new TeamMemberEntity();
        teamMember.setId(memberId);
        teamMember.setTeamId(teamId);
        teamMember.setUserId(memberUserId);
        teamMember.setRole(Role.PLAYER);
        when(teamMemberRepository.findByTeamIdAndUserId(teamId, memberUserId))
                .thenReturn(Optional.of(teamMember));

        // Mock authentication context
        UserEntity otherUser = new UserEntity();
        otherUser.setId(otherUserId);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(otherUser);

        // Create request
        RemoveTeamMemberRequest request = new RemoveTeamMemberRequest(teamId, memberUserId);

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
        String teamId = "nonExistentTeam";
        String userId = "user123";

        when(teamRepository.findById(teamId)).thenReturn(Optional.empty());

        // Create request
        RemoveTeamMemberRequest request = new RemoveTeamMemberRequest(teamId, userId);

        // Act & Assert
        assertThatThrownBy(() -> subject.removeTeamMember(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Team not found");
    }

    @Test
    void nonExistentTeamMember_shouldThrow() {
        // Arrange
        String teamId = "team123";
        String ownerId = "owner123";
        String nonExistentUserId = "nonExistentUser";

        // Mock team lookup
        TeamEntity team = new TeamEntity();
        team.setId(teamId);
        team.setOwnerId(ownerId);
        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));

        // Mock team member lookup to return empty
        when(teamMemberRepository.findByTeamIdAndUserId(teamId, nonExistentUserId))
                .thenReturn(Optional.empty());

        // Create request
        RemoveTeamMemberRequest request = new RemoveTeamMemberRequest(teamId, nonExistentUserId);

        // Act & Assert
        assertThatThrownBy(() -> subject.removeTeamMember(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Team Member not found");
    }

    @Test
    void notAuthenticated_shouldThrow() {
        // Arrange
        String teamId = "team123";
        String userId = "user123";

        // Mock team lookup
        TeamEntity team = new TeamEntity();
        team.setId(teamId);
        team.setOwnerId("owner123");
        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));

        // Mock team member lookup
        TeamMemberEntity teamMember = new TeamMemberEntity();
        teamMember.setId("member123");
        teamMember.setTeamId(teamId);
        teamMember.setUserId(userId);
        when(teamMemberRepository.findByTeamIdAndUserId(teamId, userId))
                .thenReturn(Optional.of(teamMember));

        // Mock authentication context to be null
        when(securityContext.getAuthentication()).thenReturn(null);

        // Create request
        RemoveTeamMemberRequest request = new RemoveTeamMemberRequest(teamId, userId);

        // Act & Assert
        assertThatThrownBy(() -> subject.removeTeamMember(request))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("User not authenticated");
    }
}
