package com.replan.controller;

import com.replan.business.usecases.reviewSession.SyncSessionUseCase;
import com.replan.domain.websocket.*;
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

            // Update session state in database
            syncSessionUseCase.updateSessionState(sessionId, payload.getTimestamp(), true);

            // Create and send message to all subscribers
            SessionMessage message = new SessionMessage(
                    MessageType.PLAY,
                    sessionId,
                    userInfo.getUserId(),
                    userInfo.getUsername(),
                    payload,
                    System.currentTimeMillis()
            );

            messagingTemplate.convertAndSend("/topic/session/" + sessionId, message);
            System.out.println("✅ Sent PLAY message to session: " + sessionId + " by user: " + userInfo.getUsername() + " at timestamp: " + payload.getTimestamp());
        } catch (Exception e) {
            System.err.println("❌ Error handling play: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @MessageMapping("/session/{sessionId}/pause")
    public void handlePause(@DestinationVariable String sessionId, @Payload VideoControlPayload payload, SimpMessageHeaderAccessor headerAccessor) {
        try {
            UserInfo userInfo = extractUserInfo(headerAccessor);

            // Update session state in database
            syncSessionUseCase.updateSessionState(sessionId, payload.getTimestamp(), false);

            // Create and send message to all subscribers
            SessionMessage message = new SessionMessage(
                    MessageType.PAUSE,
                    sessionId,
                    userInfo.getUserId(),
                    userInfo.getUsername(),
                    payload,
                    System.currentTimeMillis()
            );

            messagingTemplate.convertAndSend("/topic/session/" + sessionId, message);
            System.out.println("✅ Sent PAUSE message to session: " + sessionId + " by user: " + userInfo.getUsername() + " at timestamp: " + payload.getTimestamp());
        } catch (Exception e) {
            System.err.println("❌ Error handling pause: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @MessageMapping("/session/{sessionId}/seek")
    public void handleSeek(@DestinationVariable String sessionId, @Payload VideoControlPayload payload, SimpMessageHeaderAccessor headerAccessor) {
        try {
            UserInfo userInfo = extractUserInfo(headerAccessor);

            // Update session state in database
            syncSessionUseCase.updateSessionState(sessionId, payload.getTimestamp(), payload.getIsPlaying());

            // Create and send message to all subscribers
            SessionMessage message = new SessionMessage(
                    MessageType.SEEK,
                    sessionId,
                    userInfo.getUserId(),
                    userInfo.getUsername(),
                    payload,
                    System.currentTimeMillis()
            );

            messagingTemplate.convertAndSend("/topic/session/" + sessionId, message);
            System.out.println("✅ Sent SEEK message to session: " + sessionId + " by user: " + userInfo.getUsername() + " to timestamp: " + payload.getTimestamp() + " playing: " + payload.getIsPlaying());
        } catch (Exception e) {
            System.err.println("❌ Error handling seek: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @MessageMapping("/session/{sessionId}/sync")
    public void handleSyncRequest(@DestinationVariable String sessionId, SimpMessageHeaderAccessor headerAccessor) {
        try {
            // Get current session state from database
            VideoControlPayload currentState = syncSessionUseCase.getCurrentSessionState(sessionId);

            // Create sync response message
            SessionMessage message = new SessionMessage(
                    MessageType.SYNC_RESPONSE,
                    sessionId,
                    "system",
                    "System",
                    currentState,
                    System.currentTimeMillis()
            );

            // Send sync response to all subscribers
            messagingTemplate.convertAndSend("/topic/session/" + sessionId, message);
            System.out.println("✅ Sent SYNC response to session: " + sessionId + " - timestamp: " + currentState.getTimestamp() + " playing: " + currentState.getIsPlaying());
        } catch (Exception e) {
            System.err.println("❌ Error handling sync request: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @MessageMapping("/session/{sessionId}/note")
    public void handleNote(@DestinationVariable String sessionId, @Payload NotePayload notePayload, SimpMessageHeaderAccessor headerAccessor) {
        try {
            UserInfo userInfo = extractUserInfo(headerAccessor);

            // Set the author name from the authenticated user
            notePayload.setAuthorName(userInfo.getUsername());

            // Create and send note message to all subscribers
            SessionMessage message = new SessionMessage(
                    MessageType.NOTE_ADDED,
                    sessionId,
                    userInfo.getUserId(),
                    userInfo.getUsername(),
                    notePayload,
                    System.currentTimeMillis()
            );

            messagingTemplate.convertAndSend("/topic/session/" + sessionId, message);
            System.out.println("✅ Sent NOTE message to session: " + sessionId + " by user: " + userInfo.getUsername() + " at video timestamp: " + notePayload.getVideoTimestamp());
        } catch (Exception e) {
            System.err.println("❌ Error handling note: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private UserInfo extractUserInfo(SimpMessageHeaderAccessor headerAccessor) {
        try {
            // First try to get user from session attributes (set during connection)
            UserEntity storedUser = (UserEntity) headerAccessor.getSessionAttributes().get("user");
            if (storedUser != null) {
                return new UserInfo(storedUser.getId().toString(), storedUser.getUsername());
            }

            // Fallback: extract token from headers
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
                        .orElseThrow(() -> new RuntimeException("User not found for email: " + email));

                return new UserInfo(user.getId().toString(), user.getUsername());
            }

            throw new RuntimeException("No valid authentication found");
        } catch (Exception e) {
            System.err.println("❌ Authentication failed in WebSocket: " + e.getMessage());
            throw new RuntimeException("Authentication failed: " + e.getMessage());
        }
    }
}