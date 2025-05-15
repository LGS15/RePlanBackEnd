package com.replan.user;

import com.replan.business.impl.user.LoginUserImpl;
import com.replan.domain.requests.LoginUserRequest;
import com.replan.domain.responses.LoginUserResponse;
import com.replan.persistance.UserRepository;
import com.replan.persistance.entity.UserEntity;
import com.replan.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoginUserImplTest {

    @Mock
    private UserRepository userRepo;
    @Mock
    private PasswordEncoder encoder;
    @Mock
    private JwtUtil jwtUtil;
    @InjectMocks
    private LoginUserImpl subject;

    @BeforeEach
    void setUp() { MockitoAnnotations.openMocks(this); }

    @Test
    void happyPath_shouldValidateAndReturn() {
        // arrange
        String fakeId = UUID.randomUUID().toString();
        var stored = new UserEntity();
        stored.setId(fakeId);
        stored.setEmail("bob@example.com");
        stored.setUsername("bob");
        stored.setPassword("hashedpw");

        when(userRepo.findByEmail("bob@example.com"))
                .thenReturn(Optional.of(stored));
        when(encoder.matches("plain", "hashedpw")).thenReturn(true);
        when(jwtUtil.generateToken("bob@example.com")).thenReturn("jwt123");

        // act
        LoginUserResponse resp = subject.login(new LoginUserRequest("bob@example.com", "plain"));

        // assert
        assertThat(resp.getEmail()).isEqualTo("bob@example.com");
        assertThat(resp.getUsername()).isEqualTo("bob");
        assertThat(resp.getToken()).isEqualTo("jwt123");
        assertThat(resp.getUserId()).isEqualTo(fakeId);
    }

    @Test
    void badEmail_shouldThrow() {
        when(userRepo.findByEmail("nope")).thenReturn(Optional.empty());
        var request = new LoginUserRequest("nope", "x");

        assertThatThrownBy(() -> subject.login(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Invalid credentials");
    }

    @Test
    void badPassword_shouldThrow() {
        var stored = new UserEntity();
        stored.setEmail("bob@example.com");
        stored.setPassword("hashedpw");
        when(userRepo.findByEmail("bob@example.com")).thenReturn(Optional.of(stored));
        when(encoder.matches("wrong", "hashedpw")).thenReturn(false);

        var request = new LoginUserRequest("bob@example.com", "wrong");

        assertThatThrownBy(() -> subject.login(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Invalid credentials");
    }
}