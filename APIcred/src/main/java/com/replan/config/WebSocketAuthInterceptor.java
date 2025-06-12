package com.replan.config;

import com.replan.persistance.UserRepository;
import com.replan.persistance.entity.UserEntity;
import com.replan.security.JwtUtil;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public WebSocketAuthInterceptor(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null) {
            // Handle CONNECT commands - authenticate the user
            if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                authenticateUser(accessor);
            }

            // Handle DISCONNECT commands - cleanup if needed
            else if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {
                handleDisconnect(accessor);
            }

            // For other commands, ensure user is authenticated
            else if (StompCommand.SEND.equals(accessor.getCommand()) ||
                    StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {

                UserEntity user = (UserEntity) accessor.getSessionAttributes().get("user");
                if (user == null) {
                    System.err.println("❌ Unauthenticated user attempting to send message");
                    return null; // Block the message
                }
            }
        }

        return message;
    }

    private void authenticateUser(StompHeaderAccessor accessor) {
        try {
            // Extract token from STOMP headers during connection
            List<String> authHeaders = accessor.getNativeHeader("Authorization");

            if (authHeaders != null && !authHeaders.isEmpty()) {
                String authHeader = authHeaders.get(0);

                if (authHeader.startsWith("Bearer ")) {
                    String token = authHeader.substring(7);

                    if (jwtUtil.validateToken(token)) {
                        String email = jwtUtil.getEmailFromToken(token);
                        UserEntity user = userRepository.findByEmail(email).orElse(null);

                        if (user != null) {
                            // Store authentication info in session attributes
                            accessor.getSessionAttributes().put("token", token);
                            accessor.getSessionAttributes().put("user", user);
                            accessor.getSessionAttributes().put("authenticated", true);

                            System.out.println("✅ WebSocket authenticated for user: " + user.getUsername() + " (ID: " + user.getId() + ")");
                            return;
                        } else {
                            System.err.println("❌ User not found for email: " + email);
                        }
                    } else {
                        System.err.println("❌ Invalid JWT token in WebSocket connection");
                    }
                } else {
                    System.err.println("❌ Invalid Authorization header format in WebSocket connection");
                }
            } else {
                System.err.println("❌ No Authorization header found in WebSocket connection");
            }

            // If we reach here, authentication failed
            accessor.getSessionAttributes().put("authenticated", false);

        } catch (Exception e) {
            System.err.println("❌ WebSocket authentication failed: " + e.getMessage());
            e.printStackTrace();
            accessor.getSessionAttributes().put("authenticated", false);
        }
    }

    private void handleDisconnect(StompHeaderAccessor accessor) {
        try {
            UserEntity user = (UserEntity) accessor.getSessionAttributes().get("user");
            if (user != null) {
                System.out.println("🔌 WebSocket disconnected for user: " + user.getUsername());

                // Could send user left messages here if needed
                // messagingTemplate.convertAndSend("/topic/session/...", userLeftMessage);
            }
        } catch (Exception e) {
            System.err.println("❌ Error handling WebSocket disconnect: " + e.getMessage());
        }
    }
}