package com.replan.business.mapper;



import com.replan.domain.requests.AddTeamMemberRequest;
import com.replan.domain.responses.AddTeamMemberResponse;
import com.replan.persistance.entity.TeamMemberEntity;
import com.replan.persistance.entity.UserEntity;

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
        return new AddTeamMemberResponse(
                e.getId().toString(),
                e.getTeamId().toString(),
                e.getUserId().toString(),
                null,
                null,
                e.getRole()
        );
    }

    public static AddTeamMemberResponse toResponse(TeamMemberEntity e, UserEntity user) {
        if (e == null) return null;
        return new AddTeamMemberResponse(
                e.getId().toString(),
                e.getTeamId().toString(),
                e.getUserId().toString(),
                user != null ? user.getUsername() : null,
                user != null ? user.getEmail() : null,
                e.getRole()
        );
    }
}
