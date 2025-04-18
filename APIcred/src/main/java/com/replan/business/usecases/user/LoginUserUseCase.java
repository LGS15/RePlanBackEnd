package com.replan.business.usecases.user;

import com.replan.domain.requests.LoginUserRequest;
import com.replan.domain.responses.LoginUserResponse;

public interface LoginUserUseCase {
    LoginUserResponse login(LoginUserRequest request);
}
