package com.replan.controller;

import com.replan.business.usecases.user.CreateUserUseCase;
import com.replan.business.usecases.user.LoginUserUseCase;
import com.replan.domain.requests.CreateUserRequest;
import com.replan.domain.requests.LoginUserRequest;
import com.replan.domain.responses.CreateUserResponse;
import com.replan.domain.responses.LoginUserResponse;
import com.replan.persistance.UserRepository;
import com.replan.persistance.entity.UserEntity;
import com.replan.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/users")

public class UserController {

    private final CreateUserUseCase createUserUseCase;
    private final LoginUserUseCase loginUserUseCase;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public UserController(CreateUserUseCase createUserUseCase, LoginUserUseCase loginUserUseCase, UserRepository userRepository,
                          JwtUtil jwtUtil) {
        this.createUserUseCase = createUserUseCase;
        this.loginUserUseCase = loginUserUseCase;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<CreateUserResponse> register(@RequestBody CreateUserRequest request) {
        CreateUserResponse response = createUserUseCase.createUser(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    //HAHA - made you look!
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginUserRequest request) {
        try {
            LoginUserResponse response = loginUserUseCase.login(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Invalid credentials")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
            }
            throw e;
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        // Extract token from Authorization header
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);


            if (jwtUtil.validateToken(token)) {
                String email = jwtUtil.getEmailFromToken(token);


                Optional<UserEntity> userOpt = userRepository.findByEmail(email);

                if (userOpt.isPresent()) {
                    UserEntity user = userOpt.get();


                    String newToken = jwtUtil.refreshToken(token);


                    Map<String, Object> response = new HashMap<>();
                    response.put("userId", user.getId());
                    response.put("username", user.getUsername());
                    response.put("email", user.getEmail());
                    response.put("token", newToken);

                    return ResponseEntity.ok(response);
                }
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
    }
}
