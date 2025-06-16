package com.replan.domain.objects;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewSessionParticipant {

    private UUID id;
    private UUID sessionId;
    private UUID userId;
    private LocalDateTime joinedAt;
    private LocalDateTime leftAt;
    private Boolean isActive;
}
