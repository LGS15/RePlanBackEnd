package com.replan.user;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.replan.UserTestConfig;

import com.replan.business.usecases.user.CreateUserUseCase;
import com.replan.business.usecases.user.LoginUserUseCase;
import com.replan.domain.requests.CreateUserRequest;
import com.replan.domain.requests.LoginUserRequest;
import com.replan.domain.responses.CreateUserResponse;
import com.replan.domain.responses.LoginUserResponse;
import com.replan.persistance.UserRepository;
import com.replan.persistance.entity.UserEntity;

import com.replan.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;



@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@Import(UserTestConfig.class)
@TestPropertySource(properties = {
        "spring.main.allow-bean-definition-overriding=true"
})

public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LoginUserUseCase loginUserUseCase;
    @MockitoBean
    private CreateUserUseCase createUserUseCase;
    @MockitoBean
    private UserRepository userRepository;
    @MockitoBean
    private JwtUtil jwtUtil;
    @Autowired
    private ObjectMapper objectMapper;




    @BeforeEach
    void setUp() {

        reset(loginUserUseCase, createUserUseCase, userRepository, jwtUtil);
    }

    @Test
    public void loginSuccess_shouldReturnUserAndToken() throws Exception {
        // Arrange
        LoginUserRequest request = new LoginUserRequest("test@example.com", "password");
        LoginUserResponse response = new LoginUserResponse(
                "user123",
                "testuser",
                "test@example.com",
                "test-jwt-token"
        );


        given(loginUserUseCase.login(any(LoginUserRequest.class))).willReturn(response);

        // Act & Assert
        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("user123"))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.token").value("test-jwt-token"));
    }

    @Test
    public void loginFailure_invalidCredentials_shouldReturn401() throws Exception {
        // Arrange
        LoginUserRequest request = new LoginUserRequest("invalid@example.com", "wrongpassword");

        given(loginUserUseCase.login(any(LoginUserRequest.class)))
                .willThrow(new RuntimeException("Invalid credentials"));

        // Act & Assert
        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid credentials"));
    }

    @Test
    public void refreshToken_validToken_shouldReturnNewToken() throws Exception {
        // Arrange
        String validToken = "valid.jwt.token";


        UserEntity user = new UserEntity();
        user.setId("user123");
        user.setUsername("testuser");
        user.setEmail("test@example.com");


        given(jwtUtil.validateToken(validToken)).willReturn(true);
        given(jwtUtil.getEmailFromToken(validToken)).willReturn("test@example.com");
        given(jwtUtil.refreshToken(validToken)).willReturn("new.jwt.token");
        given(userRepository.findByEmail("test@example.com")).willReturn(Optional.of(user));

        // Act & Assert
        mockMvc.perform(post("/users/refresh-token")
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("user123"))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.token").value("new.jwt.token"));
    }

    @Test
    public void refreshToken_invalidToken_shouldReturn401() throws Exception {
        // Arrange
        String invalidToken = "invalid.jwt.token";


        given(jwtUtil.validateToken(invalidToken)).willReturn(false);

        // Act & Assert
        mockMvc.perform(post("/users/refresh-token")
                        .header("Authorization", "Bearer " + invalidToken))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid token"));
    }

    @Test
    public void registerUser_success_shouldReturnCreatedUser() throws Exception {
        // Arrange
        // Create request with registration data
        CreateUserRequest registerRequest = new CreateUserRequest(
                "newuser@example.com",
                "newuser",
                "password123"
        );

        // Expected response
        CreateUserResponse response = new CreateUserResponse(
                "new-user-123",
                "newuser",
                "newuser@example.com",
                "new-user-token"
        );

        // Mock createUser method using BDDMockito
        given(createUserUseCase.createUser(any(CreateUserRequest.class))).willReturn(response);

        // Act & Assert
        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value("new-user-123"))
                .andExpect(jsonPath("$.username").value("newuser"))
                .andExpect(jsonPath("$.email").value("newuser@example.com"))
                .andExpect(jsonPath("$.token").value("new-user-token"));
    }
}
