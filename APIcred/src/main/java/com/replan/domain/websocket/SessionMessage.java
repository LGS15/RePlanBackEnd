package com.replan.domain.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SessionMessage {
    private MessageType type;
    private String sessionId;
    private String userId;
    private String username;
    private Object payload;
    private Long timestamp;
}
