package com.replan.business.usecases.team;

import com.replan.domain.responses.TeamResponse;

import java.util.List;

public interface GetTeamsByUserUseCase {
    List<TeamResponse> getTeamsByUser(String userId);
}
