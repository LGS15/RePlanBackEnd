package com.replan.business.impl.teamMember;

import com.replan.business.usecases.teamMember.RemoveTeamMemberUseCase;
import com.replan.domain.requests.RemoveTeamMemberRequest;
import com.replan.domain.responses.RemoveTeamMemberResponse;
import com.replan.persistance.TeamMemberRepository;
import com.replan.persistance.TeamRepository;
import com.replan.persistance.entity.TeamEntity;
import com.replan.persistance.entity.TeamMemberEntity;
import com.replan.persistance.entity.UserEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class RemoveTeamMemberImpl implements RemoveTeamMemberUseCase {

    private final TeamMemberRepository teamMemberRepository;
    private final TeamRepository teamRepository;

    public RemoveTeamMemberImpl(TeamMemberRepository teamMemberRepository, TeamRepository teamRepository) {
        this.teamMemberRepository = teamMemberRepository;
        this.teamRepository = teamRepository;
    }

    @Override
    public RemoveTeamMemberResponse removeTeamMember(RemoveTeamMemberRequest request) {

        TeamEntity team = teamRepository.findById(request.getTeamId())
                .orElseThrow(() -> new IllegalArgumentException("Team not found"));

        TeamMemberEntity teamMember = teamMemberRepository.findByTeamIdAndUserId(request.getTeamId(), request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Team Member not found"));

        String currentUserId = getCurrentUserId();

        if(!team.getOwnerId().equals(currentUserId) && !request.getUserId().equals(currentUserId)) {
            throw new AccessDeniedException("Only the team owner can remove members");
        }

        teamMemberRepository.delete(teamMember);

        return new RemoveTeamMemberResponse(
                teamMember.getId(),
                teamMember.getTeamId(),
                teamMember.getUserId(),
                teamMember.getRole(),
                true
        );
    }

    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserEntity) {
            return ((UserEntity) authentication.getPrincipal()).getId();
        }
        throw new AccessDeniedException("User not authenticated");
    }
}
