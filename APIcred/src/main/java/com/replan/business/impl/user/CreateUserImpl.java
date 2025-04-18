package com.replan.business.impl.user;

import com.replan.business.mapper.UserMapper;
import com.replan.business.usecases.user.CreateUserUseCase;
import com.replan.domain.objects.User;
import com.replan.domain.requests.CreateUserRequest;
import com.replan.domain.responses.CreateUserResponse;
import com.replan.persistance.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;


@Service
@RequiredArgsConstructor
public class CreateUserImpl implements CreateUserUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public CreateUserResponse createUser(CreateUserRequest request) {
        // map to domain object
        User user = UserMapper.fromCreateRequest(request);

        user.setId(UUID.randomUUID());
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);

        // save to db
        userRepository.save(UserMapper.toEntity(user));

        // map to response
        return new CreateUserResponse(
                user.getId().toString(),
                user.getUsername(),
                user.getEmail()
        );
    }
}
