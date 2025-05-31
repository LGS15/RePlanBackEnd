package com.replan.domain.responses;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReviewSessionResponse {
    private String sessionId;
    private String teamId;
    private String videoUrl;
    private String title;
    private String description;
    private Long currentTimestamp;
    private Boolean isPlaying;
    private String createdBy;
    private String status;
    private Integer activeParticipants;
}
