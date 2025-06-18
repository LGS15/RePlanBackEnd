package com.replan.team;

import com.replan.business.impl.team.DeleteTeamImpl;
import com.replan.domain.requests.DeleteTeamRequest;
import com.replan.domain.responses.DeleteTeamResponse;
import com.replan.persistance.ReviewSessionParticipantRepository;
import com.replan.persistance.ReviewSessionRepository;
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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

class DeleteTeamImplTest {
    @Mock
    private TeamRepository teamRepository;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @Mock
    private ReviewSessionRepository reviewSessionRepository;

    @Mock
    private ReviewSessionParticipantRepository participantRepository;


    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private DeleteTeamImpl deleteTeamImpl;

    private static final UUID TEAM_UUID = UUID.randomUUID();
    private static final UUID OWNER_UUID = UUID.randomUUID();
    private static final UUID NON_OWNER_UUID = UUID.randomUUID();
    private static final UUID NON_EXISTENT_TEAM_UUID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void deleteTeam_happyPath_ownerDeletingTeam() {
        // Arrange
        String teamId = TEAM_UUID.toString();
        String ownerId = OWNER_UUID.toString();
        String teamName = "Test Team";

        TeamEntity team = new TeamEntity();
        team.setId(TEAM_UUID);
        team.setTeamName(teamName);
        team.setGameName("Chess");
        team.setOwnerId(OWNER_UUID);

        UserEntity ownerUser = new UserEntity();
        ownerUser.setId(OWNER_UUID);

        when(teamRepository.findById(TEAM_UUID)).thenReturn(Optional.of(team));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(ownerUser);
        when(reviewSessionRepository.findByTeamId(TEAM_UUID)).thenReturn(List.of());

        DeleteTeamRequest request = new DeleteTeamRequest(teamId);

        // Act
        DeleteTeamResponse response = deleteTeamImpl.deleteTeam(request);

        // Assert
        assertThat(response.getTeamId()).isEqualTo(teamId);
        assertThat(response.getTeamName()).isEqualTo(teamName);
        assertThat(response.isDeleted()).isTrue();

        // Verify repository
        verify(reviewSessionRepository).findByTeamId(TEAM_UUID);
        verify(participantRepository, never()).deleteBySessionId(any(UUID.class));
        verify(teamMemberRepository).deleteByTeamId(TEAM_UUID);
        verify(teamRepository).delete(team);
    }

    @Test
    void deleteTeam_nonOwnerDeletingTeam_shouldThrowAccessDeniedException() {
        // Arrange
        String teamId = TEAM_UUID.toString();

        TeamEntity team = new TeamEntity();
        team.setId(TEAM_UUID);
        team.setOwnerId(OWNER_UUID);

        UserEntity nonOwnerUser = new UserEntity();
        nonOwnerUser.setId(NON_OWNER_UUID);

        when(teamRepository.findById(TEAM_UUID)).thenReturn(Optional.of(team));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(nonOwnerUser);

        DeleteTeamRequest request = new DeleteTeamRequest(teamId);

        // Act & Assert
        assertThatThrownBy(() -> deleteTeamImpl.deleteTeam(request))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("You do not have permission to delete this team");

        // Verify no deletion occurred - use specific UUID instead of anyString()
        verify(reviewSessionRepository, never()).findByTeamId(any(UUID.class));
        verify(participantRepository, never()).deleteBySessionId(any(UUID.class));
        verify(teamMemberRepository, never()).deleteByTeamId(any(UUID.class));
        verify(teamRepository, never()).delete(any(TeamEntity.class));
    }

    @Test
    void deleteTeam_teamDoesNotExist_shouldThrowIllegalArgumentException() {
        // Arrange
        String nonExistentTeamId = NON_EXISTENT_TEAM_UUID.toString();

        when(teamRepository.findById(NON_EXISTENT_TEAM_UUID)).thenReturn(Optional.empty());

        DeleteTeamRequest request = new DeleteTeamRequest(nonExistentTeamId);

        // Act & Assert
        assertThatThrownBy(() -> deleteTeamImpl.deleteTeam(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Team not found");

        // Verify no deletion occurred
        verify(reviewSessionRepository, never()).findByTeamId(any(UUID.class));
        verify(participantRepository, never()).deleteBySessionId(any(UUID.class));
        verify(teamMemberRepository, never()).deleteByTeamId(any(UUID.class));
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

        // Verify no deletion occurred
        verify(reviewSessionRepository, never()).findByTeamId(any(UUID.class));
        verify(participantRepository, never()).deleteBySessionId(any(UUID.class));
        verify(teamRepository, never()).findById(any(UUID.class));
        verify(teamMemberRepository, never()).deleteByTeamId(any(UUID.class));
        verify(teamRepository, never()).delete(any(TeamEntity.class));
    }

}
