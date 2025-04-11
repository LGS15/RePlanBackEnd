package com.replan.persistance.dto;

import com.replan.domain.objects.Role;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class TeamMemberDto {

    private String id;
    private String teamId;
    private String userId;
    private String role;

    public TeamMemberDto(String teamId, String userId, String role) {
        this.id = UUID.randomUUID().toString();
        this.teamId = teamId;
        this.userId = userId;
        this.role = role;
    }
}