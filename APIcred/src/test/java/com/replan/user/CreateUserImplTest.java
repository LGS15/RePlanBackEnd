package com.replan.user;

import com.replan.business.impl.user.CreateUserImpl;
import com.replan.domain.requests.CreateUserRequest;
import com.replan.domain.responses.CreateUserResponse;
import com.replan.persistance.UserRepository;
import com.replan.persistance.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.mockito.*;
import com.replan.security.JwtUtil;

import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

class CreateUserImplTest {

    @Mock
    private UserRepository userRepo;
    @Mock
    private PasswordEncoder encoder;
    @Mock
    private JwtUtil jwtUtil;
    @InjectMocks
    private CreateUserImpl subject;

    @BeforeEach
    void setUp() { MockitoAnnotations.openMocks(this); }

    @Test
    void happyPath_shouldEncodeSaveAndReturnToken() {
        var req = new CreateUserRequest("alice@example.com", "alice", "plainpw");
        when(encoder.encode("plainpw")).thenReturn("hashedpw");
        when(jwtUtil.generateToken("alice@example.com")).thenReturn("tok123");

        UUID generatedId = UUID.randomUUID();
        when(userRepo.save(any(UserEntity.class))).thenAnswer(inv -> {
            UserEntity savedEntity = new UserEntity();
            UserEntity inputEntity = inv.getArgument(0);
            savedEntity.setId(generatedId);
            savedEntity.setEmail(inputEntity.getEmail());
            savedEntity.setUsername(inputEntity.getUsername());
            savedEntity.setPassword(inputEntity.getPassword());
            return savedEntity;
        });

        CreateUserResponse resp = subject.createUser(req);

        assertThat(resp.getEmail()).isEqualTo("alice@example.com");
        assertThat(resp.getUsername()).isEqualTo("alice");
        assertThat(resp.getToken()).isEqualTo("tok123");
        assertThat(resp.getUserId()).isEqualTo(generatedId.toString());

        verify(userRepo).save(argThat(ent ->
                ent.getPassword().equals("hashedpw")
                        && ent.getEmail().equals("alice@example.com")
                        && ent.getUsername().equals("alice")
        ));
    }
}

