package com.replan.business.impl.user;

import com.replan.business.mapper.UserMapper;
import com.replan.business.usecases.user.CreateUserUseCase;
import com.replan.domain.objects.User;
import com.replan.domain.requests.CreateUserRequest;
import com.replan.domain.responses.CreateUserResponse;
import com.replan.persistance.UserRepository;
import com.replan.persistance.entity.UserEntity;
import com.replan.security.JwtUtil;
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
    private final JwtUtil jwtUtil;

    @Override
    @Transactional
    public CreateUserResponse createUser(CreateUserRequest request) {

        UserEntity entity = UserMapper.toEntity(
                UserMapper.fromCreateRequest(request)
        );

        entity.setPassword(passwordEncoder.encode(entity.getPassword()));

        UserEntity saved = userRepository.save(entity);

        String token = jwtUtil.generateToken(saved.getEmail());

        return new CreateUserResponse(
                saved.getId().toString(),
                saved.getUsername(),
                saved.getEmail(),
                token
        );
    }
}
