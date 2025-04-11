package com.replan.business.mapper;

import com.replan.domain.objects.Role;
import com.replan.domain.objects.TeamMember;
import com.replan.persistance.dto.TeamMemberDto;

public class TeamMemberMapper {

    public static TeamMember toEntity(TeamMemberDto dto) {
        TeamMember member = new TeamMember();
        member.setId(dto.getId());
        member.setTeamId(dto.getTeamId());
        member.setUserId(dto.getUserId());
        member.setRole(Role.valueOf(dto.getRole()));
        return member;
    }

    public static TeamMemberDto toDto(TeamMember entity) {
        TeamMemberDto dto = new TeamMemberDto();
        dto.setId(entity.getId());
        dto.setTeamId(entity.getTeamId());
        dto.setUserId(entity.getUserId());
        dto.setRole(entity.getRole().name());
        return dto;
    }
}
