package com.replan;

import com.replan.business.impl.user.CreateUserImpl;
import com.replan.business.impl.user.LoginUserImpl;
import com.replan.business.usecases.user.CreateUserUseCase;
import com.replan.business.usecases.user.LoginUserUseCase;
import com.replan.persistance.UserRepository;
import com.replan.security.JwtUtil;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@TestConfiguration
public class UserTestConfig {
    @Bean
    @Primary
    public UserRepository userRepository() {
        return Mockito.mock(UserRepository.class);
    }


    @Bean
    @Primary
    public JwtUtil jwtUtil() {
        return Mockito.mock(JwtUtil.class);
    }

    @Bean
    @Primary
    public CreateUserUseCase createUserUseCase() {
        return Mockito.mock(CreateUserUseCase.class);
    }

    @Bean
    @Primary
    public LoginUserUseCase loginUserUseCase() {
        return Mockito.mock(LoginUserUseCase.class);
    }

    @Bean
    @Primary
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
