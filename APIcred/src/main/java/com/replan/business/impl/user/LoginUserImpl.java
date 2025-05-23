package com.replan.business.impl.user;

import com.replan.business.mapper.UserMapper;
import com.replan.business.usecases.user.LoginUserUseCase;
import com.replan.domain.objects.User;
import com.replan.domain.requests.LoginUserRequest;
import com.replan.domain.responses.LoginUserResponse;
import com.replan.persistance.UserRepository;
import com.replan.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginUserImpl implements LoginUserUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;


    @Override
    public LoginUserResponse login(LoginUserRequest request) {
        var userEntity = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), userEntity.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        User user = UserMapper.toDomain(userEntity);


        String token = jwtUtil.generateToken(user.getEmail());

        return new LoginUserResponse(
                user.getId().toString(),
                user.getUsername(),
                user.getEmail(),
                token
        );
    }
}