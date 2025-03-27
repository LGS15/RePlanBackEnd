package com.replan.business.usecases.team;


import com.replan.domain.responses.CreateTeamResponse;
import com.replan.domain.requests.CreateTeamRequest;

public interface CreateTeamUseCase {
    CreateTeamResponse createTeam(CreateTeamRequest request);
}
