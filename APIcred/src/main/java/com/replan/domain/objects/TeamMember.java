package com.replan.domain.objects;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class TeamMember {

    private String id;
    private String teamId;
    private String userId;
    private Role role;

    public TeamMember(String teamId, String userId, Role role) {
        this.id = UUID.randomUUID().toString();
        this.teamId = teamId;
        this.userId = userId;
        this.role = role;
    }
}
