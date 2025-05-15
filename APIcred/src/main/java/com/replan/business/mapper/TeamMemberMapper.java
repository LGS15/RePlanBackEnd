package com.replan.business.mapper;



import com.replan.domain.requests.AddTeamMemberRequest;
import com.replan.domain.responses.AddTeamMemberResponse;
import com.replan.persistance.entity.TeamMemberEntity;

public class TeamMemberMapper {

    private TeamMemberMapper() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static TeamMemberEntity toEntity(AddTeamMemberRequest d, String userId) {
        TeamMemberEntity e = new TeamMemberEntity();

        e.setTeamId(d.getTeamId());
        e.setUserId(userId);
        e.setRole(d.getRole());
        return e;
    }

    public static AddTeamMemberResponse toResponse(TeamMemberEntity e) {
        if (e == null) return null;
        AddTeamMemberResponse m = new AddTeamMemberResponse(
        e.getId(),
        e.getTeamId(),
        e.getUserId(),
        e.getRole()
        );
        return m;
    }
}
