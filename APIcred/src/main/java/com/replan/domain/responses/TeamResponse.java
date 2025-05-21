package com.replan.domain.responses;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class TeamResponse {
    public String teamId;
    public String teamName;
    public String gameName;
    public String ownerId;
}
