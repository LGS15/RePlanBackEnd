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
        // echo back when saved
        when(userRepo.save(any(UserEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        CreateUserResponse resp = subject.createUser(req);

        assertThat(resp.getEmail()).isEqualTo("alice@example.com");
        assertThat(resp.getUsername()).isEqualTo("alice");
        assertThat(resp.getToken()).isEqualTo("tok123");
        // id should be non-null UUID string
        assertThat(resp.getUserId()).matches("[0-9a-fA-F\\-]{36}");
        // ensure that we saved a UserEntity with hashed password
        verify(userRepo).save(argThat(ent ->
                ent.getPassword().equals("hashedpw")
                        && ent.getEmail().equals("alice@example.com")
                        && ent.getUsername().equals("alice")
        ));
    }
}
