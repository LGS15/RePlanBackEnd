package com.replan.business.impl;

import com.replan.business.usecases.team.CreateTeamUseCase;
import com.replan.domain.objects.Team;
import com.replan.domain.requests.CreateTeamRequest;
import com.replan.domain.responses.CreateTeamResponse;
import com.replan.persistance.TeamRepository;
import org.springframework.stereotype.Service;

@Service
public class CreateTeamImpl  implements CreateTeamUseCase {

    private final TeamRepository teamRepository;

    public CreateTeamImpl(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

   @Override
    public CreateTeamResponse createTeam(CreateTeamRequest request) {
        if (request.getTeamName() == null || request.getTeamName().isEmpty()) {
            throw new IllegalArgumentException("Team name cannot be empty");
        }
        if (request.getGameName() == null || request.getGameName().isEmpty()) {
            throw new IllegalArgumentException("Game name cannot be empty");
        }
        if (request.getOwnerId() == null || request.getOwnerId().isEmpty()) {
            throw new IllegalArgumentException("Owner id cannot be empty");
        }

        Team newTeam = new Team(
                request.getTeamName(),
                request.getGameName(),
                request.getOwnerId()
        );

        Team savedTeam = teamRepository.save(newTeam);

        return new CreateTeamResponse(
                savedTeam.getId(),
                savedTeam.getTeamName(),
                savedTeam.getGameName(),
                savedTeam.getOwnerId()
        );
   }
}
