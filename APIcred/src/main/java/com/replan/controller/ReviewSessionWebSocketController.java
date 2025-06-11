package com.replan.controller;

import com.replan.business.usecases.reviewSession.SyncSessionUseCase;
import com.replan.domain.websocket.MessageType;
import com.replan.domain.websocket.NotePayload;
import com.replan.domain.websocket.SessionMessage;
import com.replan.domain.websocket.VideoControlPayload;
import com.replan.persistance.UserRepository;
import com.replan.persistance.entity.UserEntity;
import com.replan.security.JwtUtil;
import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

@Controller
@AllArgsConstructor
public class ReviewSessionWebSocketController {


    private final SimpMessagingTemplate messagingTemplate;
    private final SyncSessionUseCase syncSessionUseCase;

    @MessageMapping("/session/{sessionId}/play")
    public void handlePlay(@DestinationVariable String sessionId, @Payload VideoControlPayload payload) {
        try {
            syncSessionUseCase.updateSessionState(sessionId, payload.getTimestamp(), true);

            SessionMessage message = new SessionMessage(
                    MessageType.PLAY,
                    sessionId,
                    "test-user",
                    "Test User",
                    payload,
                    System.currentTimeMillis()
            );

            messagingTemplate.convertAndSend("/topic/session/" + sessionId, message);
            System.out.println("Sent PLAY message to session: " + sessionId);
        } catch (Exception e) {
            System.err.println("Error handling play: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @MessageMapping("/session/{sessionId}/pause")
    public void handlePause(@DestinationVariable String sessionId, @Payload VideoControlPayload payload) {
        try {
            syncSessionUseCase.updateSessionState(sessionId, payload.getTimestamp(), false);

            SessionMessage message = new SessionMessage(
                    MessageType.PAUSE,
                    sessionId,
                    "test-user",
                    "Test User",
                    payload,
                    System.currentTimeMillis()
            );

            messagingTemplate.convertAndSend("/topic/session/" + sessionId, message);
            System.out.println("Sent PAUSE message to session: " + sessionId);
        } catch (Exception e) {
            System.err.println("Error handling pause: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @MessageMapping("/session/{sessionId}/seek")
    public void handleSeek(@DestinationVariable String sessionId, @Payload VideoControlPayload payload) {
        try {
            syncSessionUseCase.updateSessionState(sessionId, payload.getTimestamp(), payload.getIsPlaying());

            SessionMessage message = new SessionMessage(
                    MessageType.SEEK,
                    sessionId,
                    "test-user",
                    "Test User",
                    payload,
                    System.currentTimeMillis()
            );

            messagingTemplate.convertAndSend("/topic/session/" + sessionId, message);
            System.out.println("Sent SEEK message to session: " + sessionId);
        } catch (Exception e) {
            System.err.println("Error handling seek: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @MessageMapping("/session/{sessionId}/sync")
    public void handleSyncRequest(@DestinationVariable String sessionId) {
        try {
            VideoControlPayload currentState = syncSessionUseCase.getCurrentSessionState(sessionId);

            SessionMessage message = new SessionMessage(
                    MessageType.SYNC_RESPONSE,
                    sessionId,
                    "system",
                    "system",
                    currentState,
                    System.currentTimeMillis()
            );

            messagingTemplate.convertAndSend("/topic/session/" + sessionId, message);
            System.out.println("Sent SYNC message to session: " + sessionId);
        } catch (Exception e) {
            System.err.println("Error handling sync request: " + e.getMessage());
            e.printStackTrace();
        }
    }
}