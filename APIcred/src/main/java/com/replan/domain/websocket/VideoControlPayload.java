package com.replan.domain.websocket;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoControlPayload {
    private Long timestamp;
    private Boolean isPlaying;
}
