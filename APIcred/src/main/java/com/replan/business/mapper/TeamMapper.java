package com.replan.business.mapper;

import com.replan.domain.requests.CreateTeamRequest;
import com.replan.domain.responses.CreateTeamResponse;
import com.replan.domain.responses.TeamResponse;
import com.replan.persistance.entity.TeamEntity;

import java.util.UUID;

public class TeamMapper {

    private TeamMapper() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static TeamEntity toEntity(CreateTeamRequest req) {
        TeamEntity e = new TeamEntity();
        e.setTeamName(req.getTeamName());
        e.setGameName(req.getGameName());
        e.setOwnerId(UUID.fromString(req.getOwnerId()));
        return e;
    }

    public static CreateTeamResponse toCreateResponse(TeamEntity e) {
        return new CreateTeamResponse(
                e.getId().toString(),
                e.getTeamName(),
                e.getGameName(),
                e.getOwnerId().toString()
        );
    }

    public static TeamResponse toTeamResponse(TeamEntity e) {
        return new TeamResponse(
                e.getId().toString(),
                e.getTeamName(),
                e.getGameName(),
                e.getOwnerId().toString()
        );
    }
}
