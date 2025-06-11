package com.replan.controller;

import com.replan.business.usecases.reviewSession.SyncSessionUseCase;
import com.replan.domain.websocket.MessageType;
import com.replan.domain.websocket.SessionMessage;
import com.replan.domain.websocket.UserInfo;
import com.replan.domain.websocket.VideoControlPayload;
import com.replan.persistance.UserRepository;
import com.replan.persistance.entity.UserEntity;
import com.replan.security.JwtUtil;
import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@AllArgsConstructor
public class ReviewSessionWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final SyncSessionUseCase syncSessionUseCase;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @MessageMapping("/session/{sessionId}/play")
    public void handlePlay(@DestinationVariable String sessionId, @Payload VideoControlPayload payload, SimpMessageHeaderAccessor headerAccessor) {
        try {
            UserInfo userInfo = extractUserInfo(headerAccessor);

            syncSessionUseCase.updateSessionState(sessionId, payload.getTimestamp(), true);

            SessionMessage message = new SessionMessage(
                    MessageType.PLAY,
                    sessionId,
                    userInfo.getUserId(),
                    userInfo.getUsername(),
                    payload,
                    System.currentTimeMillis()
            );

            messagingTemplate.convertAndSend("/topic/session/" + sessionId, message);
            System.out.println("Sent PLAY message to session: " + sessionId + " by user: " + userInfo.getUsername());
        } catch (Exception e) {
            System.err.println("Error handling play: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @MessageMapping("/session/{sessionId}/pause")
    public void handlePause(@DestinationVariable String sessionId, @Payload VideoControlPayload payload, SimpMessageHeaderAccessor headerAccessor) {
        try {
            UserInfo userInfo = extractUserInfo(headerAccessor);

            syncSessionUseCase.updateSessionState(sessionId, payload.getTimestamp(), false);

            SessionMessage message = new SessionMessage(
                    MessageType.PAUSE,
                    sessionId,
                    userInfo.getUserId(),
                    userInfo.getUsername(),
                    payload,
                    System.currentTimeMillis()
            );

            messagingTemplate.convertAndSend("/topic/session/" + sessionId, message);
            System.out.println("Sent PAUSE message to session: " + sessionId + " by user: " + userInfo.getUsername());
        } catch (Exception e) {
            System.err.println("Error handling pause: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @MessageMapping("/session/{sessionId}/seek")
    public void handleSeek(@DestinationVariable String sessionId, @Payload VideoControlPayload payload, SimpMessageHeaderAccessor headerAccessor) {
        try {
            UserInfo userInfo = extractUserInfo(headerAccessor);

            syncSessionUseCase.updateSessionState(sessionId, payload.getTimestamp(), payload.getIsPlaying());

            SessionMessage message = new SessionMessage(
                    MessageType.SEEK,
                    sessionId,
                    userInfo.getUserId(),
                    userInfo.getUsername(),
                    payload,
                    System.currentTimeMillis()
            );

            messagingTemplate.convertAndSend("/topic/session/" + sessionId, message);
            System.out.println("Sent SEEK message to session: " + sessionId + " by user: " + userInfo.getUsername());
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
                    "System",
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

    private UserInfo extractUserInfo(SimpMessageHeaderAccessor headerAccessor) {
        try {
            // token from stomp headers
            String token = (String) headerAccessor.getSessionAttributes().get("token");

            if (token == null) {
                List<String> authHeaders = headerAccessor.getNativeHeader("Authorization");
                if (authHeaders != null && !authHeaders.isEmpty()) {
                    String authHeader = authHeaders.get(0);
                    if (authHeader.startsWith("Bearer ")) {
                        token = authHeader.substring(7);
                    }
                }
            }

            if (token != null && jwtUtil.validateToken(token)) {
                String email = jwtUtil.getEmailFromToken(token);
                UserEntity user = userRepository.findByEmail(email)
                        .orElseThrow(() -> new RuntimeException("User not found"));

                return new UserInfo(user.getId().toString(), user.getUsername());
            }

            throw new RuntimeException("Invalid or missing authentication token");
        } catch (Exception e) {
            throw new RuntimeException("Authentication failed: " + e.getMessage());
        }
    }


}