package com.dev.quikkkk.user_service.service;

import com.dev.quikkkk.user_service.dto.request.CreateUserRequest;
import com.dev.quikkkk.user_service.dto.request.UpdateUserRequest;
import com.dev.quikkkk.user_service.dto.response.UserResponse;

import java.util.List;


public interface IUserService {
    void createUser(CreateUserRequest request);

    UserResponse getUserById(String id);

    List<UserResponse> getUsersByRole(String role);

    UserResponse updateUser(String id, UpdateUserRequest request);

    void deleteUser(String id);

    List<UserResponse> searchUsers(String name);
}
