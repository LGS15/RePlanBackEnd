package com.replan.team;

import com.replan.business.impl.team.DeleteTeamImpl;
import com.replan.domain.requests.DeleteTeamRequest;
import com.replan.domain.responses.DeleteTeamResponse;
import com.replan.persistance.TeamMemberRepository;
import com.replan.persistance.TeamRepository;
import com.replan.persistance.entity.TeamEntity;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

class DeleteTeamImplTest {
    @Mock
    private TeamRepository teamRepository;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private DeleteTeamImpl deleteTeamImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void deleteTeam_happyPath_ownerDeletingTeam() {
        // Arrange
        String teamId = "team123";
        String ownerId = "owner123";
        String teamName = "Test Team";


        TeamEntity team = new TeamEntity();
        team.setId(teamId);
        team.setTeamName(teamName);
        team.setGameName("Chess");
        team.setOwnerId(ownerId);


        UserEntity ownerUser = new UserEntity();
        ownerUser.setId(UUID.fromString(ownerId));


        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(ownerUser);

        DeleteTeamRequest request = new DeleteTeamRequest(teamId);

        // Act
        DeleteTeamResponse response = deleteTeamImpl.deleteTeam(request);

        // Assert
        assertThat(response.getTeamId()).isEqualTo(teamId);
        assertThat(response.getTeamName()).isEqualTo(teamName);
        assertThat(response.isDeleted()).isTrue();

        // Verify repository
        verify(teamMemberRepository).deleteByTeamId(teamId);
        verify(teamRepository).delete(team);
    }

    @Test
    void deleteTeam_nonOwnerDeletingTeam_shouldThrowAccessDeniedException() {
        // Arrange
        String teamId = "team123";
        String ownerId = "owner123";
        String nonOwnerId = "user456";


        TeamEntity team = new TeamEntity();
        team.setId(teamId);
        team.setOwnerId(ownerId);


        UserEntity nonOwnerUser = new UserEntity();
        nonOwnerUser.setId(UUID.fromString(nonOwnerId));


        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(nonOwnerUser);

        DeleteTeamRequest request = new DeleteTeamRequest(teamId);

        // Act & Assert
        assertThatThrownBy(() -> deleteTeamImpl.deleteTeam(request))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("You do not have permission to delete this team");


        verify(teamMemberRepository, never()).deleteByTeamId(anyString());
        verify(teamRepository, never()).delete(any(TeamEntity.class));
    }

    @Test
    void deleteTeam_teamDoesNotExist_shouldThrowIllegalArgumentException() {
        // Arrange
        String nonExistentTeamId = "nonExistentTeam";


        when(teamRepository.findById(nonExistentTeamId)).thenReturn(Optional.empty());

        DeleteTeamRequest request = new DeleteTeamRequest(nonExistentTeamId);

        // Act & Assert
        assertThatThrownBy(() -> deleteTeamImpl.deleteTeam(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Team not found");

        // Verify no deletion occurred
        verify(teamMemberRepository, never()).deleteByTeamId(anyString());
        verify(teamRepository, never()).delete(any(TeamEntity.class));
    }

    @Test
    void deleteTeam_emptyTeamId_shouldThrowIllegalArgumentException() {
        // Arrange
        DeleteTeamRequest request = new DeleteTeamRequest("");

        // Act & Assert
        assertThatThrownBy(() -> deleteTeamImpl.deleteTeam(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Team id cannot be empty");

        // Verify no repository calls were made
        verify(teamRepository, never()).findById(anyString());
        verify(teamMemberRepository, never()).deleteByTeamId(anyString());
        verify(teamRepository, never()).delete(any(TeamEntity.class));
    }

}
