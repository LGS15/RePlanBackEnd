package com.replan.business.usecases.user;

import com.replan.domain.requests.CreateUserRequest;
import com.replan.domain.responses.CreateUserResponse;

public interface CreateUserUseCase {
    CreateUserResponse createUser(CreateUserRequest request);
}
