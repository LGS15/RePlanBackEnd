package com.replan.domain.responses;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JoinSessionResponse {
    private String sessionId;
    private String message;
    private Long currentTimestamp;
    private Boolean isPlaying;
    private Integer activeParticipants;
}