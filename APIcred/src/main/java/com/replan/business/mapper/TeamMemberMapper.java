package com.replan.business.mapper;



import com.replan.domain.requests.AddTeamMemberRequest;
import com.replan.domain.responses.AddTeamMemberResponse;
import com.replan.persistance.entity.TeamMemberEntity;

import java.util.UUID;

public class TeamMemberMapper {

    private TeamMemberMapper() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static TeamMemberEntity toEntity(AddTeamMemberRequest d, String userId) {
        TeamMemberEntity e = new TeamMemberEntity();

        e.setTeamId(UUID.fromString(d.getTeamId()));
        e.setUserId(UUID.fromString(userId));
        e.setRole(d.getRole());
        return e;
    }

    public static AddTeamMemberResponse toResponse(TeamMemberEntity e) {
        if (e == null) return null;
        AddTeamMemberResponse m = new AddTeamMemberResponse(
        e.getId().toString(),
        e.getTeamId().toString(),
        e.getUserId().toString(),
        e.getRole()
        );
        return m;
    }
}
