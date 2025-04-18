package com.replan;

import com.replan.business.impl.user.CreateUserImpl;
import com.replan.business.impl.user.LoginUserImpl;
import com.replan.business.usecases.user.CreateUserUseCase;
import com.replan.business.usecases.user.LoginUserUseCase;
import com.replan.persistance.UserRepository;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@TestConfiguration
public class UserTestConfig {
    @Bean
    public UserRepository userRepository() {
        return Mockito.mock(UserRepository.class);
    }

    @Bean
    public CreateUserUseCase createUserUseCase(UserRepository userRepository, BCryptPasswordEncoder encoder) {
        return new CreateUserImpl(userRepository, encoder);
    }

    @Bean
    public LoginUserUseCase loginUserUseCase(UserRepository userRepository, BCryptPasswordEncoder encoder) {
        return new LoginUserImpl(userRepository, encoder);
    }

    @Bean
    @Primary
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
