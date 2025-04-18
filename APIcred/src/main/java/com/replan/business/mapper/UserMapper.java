package com.replan.business.mapper;


import com.replan.domain.objects.User;
import com.replan.domain.requests.CreateUserRequest;
import com.replan.domain.requests.LoginUserRequest;
import com.replan.persistance.dto.UserDTO;
import com.replan.persistance.entity.UserEntity;

public class UserMapper {

    public static User toDomain(UserEntity entity) {
        if (entity == null) return null;
        return new User(
                entity.getId(),
                entity.getEmail(),
                entity.getUsername(),
                entity.getPassword()
        );
    }

    public static UserEntity toEntity(User user) {
        if (user == null) return null;
        UserEntity entity = new UserEntity();
        entity.setId(user.getId());
        entity.setEmail(user.getEmail());
        entity.setUsername(user.getUsername());
        entity.setPassword(user.getPassword());
        return entity;
    }

    public static UserDTO toDTO(User user) {
        if (user == null) return null;
        return new UserDTO(
                user.getId(),
                user.getEmail(),
                user.getUsername()
        );
    }

    public static User fromCreateRequest(CreateUserRequest request) {
        if (request == null) return null;
        return new User(
                null,
                request.getEmail(),
                request.getUsername(),
                request.getPassword()
        );
    }

    public static User fromLoginRequest(LoginUserRequest request) {
        if (request == null) return null;
        return new User(
                null,
                request.getEmail(),
                null,
                request.getPassword()
        );
    }
}
