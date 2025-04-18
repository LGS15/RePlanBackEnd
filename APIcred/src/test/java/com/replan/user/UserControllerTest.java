package com.replan.user;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.replan.UserTestConfig;

import com.replan.persistance.UserRepository;
import com.replan.persistance.entity.UserEntity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;

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
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void resetMocks() {
        reset(userRepository);
    }

    @Test
    void testRegisterUser() throws Exception {
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> {
            UserEntity user = invocation.getArgument(0);
            user.setId(UUID.randomUUID());
            return user;
        });

        mockMvc.perform(post("/users/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
            {
              "email": "mikochi@example.com",
              "username": "Mikochi",
              "password": "secret123"
            }
        """))
                .andExpect(status().isCreated());
    }

    @Test
    void testLoginUser() throws Exception {
        String rawPassword = "secret123";

        UserEntity user = new UserEntity();
        user.setId(UUID.randomUUID());
        user.setEmail("mikochi@example.com");
        user.setUsername("Mikochi");
        user.setPassword(passwordEncoder.encode(rawPassword));

        when(userRepository.findByEmail("mikochi@example.com")).thenReturn(Optional.of(user));

        mockMvc.perform(post("/users/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
            {
              "email": "mikochi@example.com",
              "password": "secret123"
            }
        """))
                .andExpect(status().isOk());
    }
}