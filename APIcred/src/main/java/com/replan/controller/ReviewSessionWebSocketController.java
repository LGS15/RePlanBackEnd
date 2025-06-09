package com.replan.controller;

import com.replan.business.usecases.reviewSession.SyncSessionUseCase;
import com.replan.domain.websocket.MessageType;
import com.replan.domain.websocket.NotePayload;
import com.replan.domain.websocket.SessionMessage;
import com.replan.domain.websocket.VideoControlPayload;
import com.replan.persistance.UserRepository;
import com.replan.persistance.entity.UserEntity;
import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
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
    private final UserRepository userRepository;

    @MessageMapping("/session/{sessionId}/play")
    public void handlePlay(@DestinationVariable String sessionId, @Payload VideoControlPayload payload) {
        UserEntity currentUser = getCurrentUser();
        syncSessionUseCase.updateSessionState(sessionId, payload.getTimestamp(), true);

        SessionMessage message = new SessionMessage(
                MessageType.PLAY,
                sessionId,
                currentUser.getId().toString(),
                currentUser.getUsername(),
                payload,
                System.currentTimeMillis()
        );

        messagingTemplate.convertAndSend("/topic/session/" + sessionId, message);
    }

    @MessageMapping("/session/{sessionId}/pause")
    public void handlePause(@DestinationVariable String sessionId, @Payload VideoControlPayload payload) {
        UserEntity currentUser = getCurrentUser();
        syncSessionUseCase.updateSessionState(sessionId, payload.getTimestamp(), false);

        SessionMessage message = new SessionMessage(
                MessageType.PAUSE,
                sessionId,
                currentUser.getId().toString(),
                currentUser.getUsername(),
                payload,
                System.currentTimeMillis()
        );

        messagingTemplate.convertAndSend("/topic/session/" + sessionId, message);
    }

    @MessageMapping("/session/{sessionId}/seek")
    public void handleSeek(@DestinationVariable String sessionId, @Payload VideoControlPayload payload) {
        UserEntity currentUser = getCurrentUser();
        syncSessionUseCase.updateSessionState(sessionId, payload.getTimestamp(), payload.getIsPlaying());

        SessionMessage message = new SessionMessage(
                MessageType.SEEK,
                sessionId,
                currentUser.getId().toString(),
                currentUser.getUsername(),
                payload,
                System.currentTimeMillis()
        );

        messagingTemplate.convertAndSend("/topic/session/" + sessionId, message);
    }

    @MessageMapping("/session/{sessionId}/note")
    public void handleNote(@DestinationVariable String sessionId, @Payload NotePayload payload) {
        UserEntity currentUser = getCurrentUser();

        payload.setAuthorName(currentUser.getUsername());

        SessionMessage message = new SessionMessage(
                MessageType.NOTE_ADDED,
                sessionId,
                currentUser.getId().toString(),
                currentUser.getUsername(),
                payload,
                System.currentTimeMillis()
        );

        messagingTemplate.convertAndSend("/topic/session/" + sessionId, message);
    }

    @MessageMapping("/session/{sessionId}/sync")
    public void handleSyncRequest(@DestinationVariable String sessionId) {
        UserEntity currentUser = getCurrentUser();
        VideoControlPayload currentState = syncSessionUseCase.getCurrentSessionState(sessionId);

        SessionMessage message = new SessionMessage(
                MessageType.SYNC_RESPONSE,
                sessionId,
                "system",
                "system",
                currentState,
                System.currentTimeMillis()
        );

        messagingTemplate.convertAndSendToUser(
                currentUser.getEmail(),
                "/topic/session/" + sessionId + "/sync",
                message
        );
    }

    private UserEntity getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserEntity) {
            return (UserEntity) authentication.getPrincipal();
        }
        throw new IllegalStateException("User not authenticated");
    }
}