package com.replan.domain.objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewSession {

    private UUID id;
    private UUID teamId;
    private String videoUrl;
    private String title;
    private String description;
    private Long currentTimeStamp;
    private Boolean isPlaying;
    private UUID createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime endedAt;
    private SessionStatus status;


}
