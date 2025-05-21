package com.replan.business.impl.team;

import com.replan.business.usecases.team.DeleteTeamUseCase;
import com.replan.domain.requests.DeleteTeamRequest;
import com.replan.domain.responses.DeleteTeamResponse;
import com.replan.persistance.TeamMemberRepository;
import com.replan.persistance.TeamRepository;
import com.replan.persistance.entity.TeamEntity;
import com.replan.persistance.entity.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DeleteTeamImpl implements DeleteTeamUseCase {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;

    public DeleteTeamImpl(TeamRepository teamRepository, TeamMemberRepository teamMemberRepository) {
        this.teamRepository = teamRepository;
        this.teamMemberRepository = teamMemberRepository;
    }

    @Override
    @Transactional
    public DeleteTeamResponse deleteTeam(DeleteTeamRequest request){

        if(request.getTeamId() == null || request.getTeamId().isEmpty()){
            throw new IllegalArgumentException("Team id cannot be empty");
        }

        TeamEntity team = teamRepository.findById(request.getTeamId())
                .orElseThrow(()-> new IllegalArgumentException("Team not found"));

        UUID currentUserId = getCurrentUserId();
        if (!team.getOwnerId().equals(currentUserId)){
            throw new AccessDeniedException("You do not have permission to delete this team");
        }

        String teamName = team.getTeamName();

        teamMemberRepository.deleteByTeamId(request.getTeamId());
        teamRepository.delete(team);

        return new DeleteTeamResponse(
                request.getTeamId(),
                teamName,
                true,
                "Team successfully deleted"
        );
    };

    private UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserEntity) {
            return ((UserEntity) authentication.getPrincipal()).getId();
        }
        throw new AccessDeniedException("User not authenticated");
    }
}
