package com.replan.domain.objects;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class Team {
    private String id;
    private String teamName;
    private String gameName;
    private String ownerId;

    public Team( String teamName, String gameName, String ownerId) {
        this.id = UUID.randomUUID().toString();
        this.teamName = teamName;
        this.gameName = gameName;
        this.ownerId = ownerId;
    }
}
