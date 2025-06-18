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
            if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                authenticateUser(accessor);
            }

            else if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {
                handleDisconnect(accessor);
            }

            else if (StompCommand.SEND.equals(accessor.getCommand()) ||
                    StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {

                UserEntity user = (UserEntity) accessor.getSessionAttributes().get("user");
                Boolean isAuthenticated = (Boolean) accessor.getSessionAttributes().get("authenticated");

                if (user == null || !Boolean.TRUE.equals(isAuthenticated)) {
                    System.out.println(" WebSocket security disabled - allowing unauthenticated "
                            + accessor.getCommand() + " to " + accessor.getDestination());
                } else {
                    System.out.println(" Authenticated user " + user.getUsername()
                            + " sending " + accessor.getCommand() + " to " + accessor.getDestination());
                }
            }
        }

        return message;
    }

    private void authenticateUser(StompHeaderAccessor accessor) {
        try {
            List<String> authHeaders = accessor.getNativeHeader("Authorization");

            if (authHeaders != null && !authHeaders.isEmpty()) {
                String authHeader = authHeaders.get(0);

                if (authHeader.startsWith("Bearer ")) {
                    String token = authHeader.substring(7);

                    if (jwtUtil.validateToken(token)) {
                        String email = jwtUtil.getEmailFromToken(token);
                        UserEntity user = userRepository.findByEmail(email).orElse(null);

                        if (user != null) {

                            accessor.getSessionAttributes().put("token", token);
                            accessor.getSessionAttributes().put("user", user);
                            accessor.getSessionAttributes().put("authenticated", true);

                            System.out.println(" WebSocket authenticated for user: " + user.getUsername() + " (ID: " + user.getId() + ")");
                            return;
                        } else {
                            System.err.println(" User not found for email: " + email);
                        }
                    } else {
                        System.err.println(" Invalid JWT token in WebSocket connection");
                    }
                } else {
                    System.err.println(" Invalid Authorization header format in WebSocket connection");
                }
            } else {
                System.err.println(" No Authorization header found in WebSocket connection");
            }

            // If we reach here, authentication failed
            accessor.getSessionAttributes().put("authenticated", false);

        } catch (Exception e) {
            System.err.println(" WebSocket authentication failed: " + e.getMessage());
            e.printStackTrace();
            accessor.getSessionAttributes().put("authenticated", false);
        }
    }

    private void handleDisconnect(StompHeaderAccessor accessor) {
        try {
            UserEntity user = (UserEntity) accessor.getSessionAttributes().get("user");
            if (user != null) {
                System.out.println(" WebSocket disconnected for user: " + user.getUsername());


            }
        } catch (Exception e) {
            System.err.println(" Error handling WebSocket disconnect: " + e.getMessage());
        }
    }
}