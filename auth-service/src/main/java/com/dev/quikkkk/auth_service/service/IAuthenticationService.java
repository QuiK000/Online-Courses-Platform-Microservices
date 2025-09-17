package com.dev.quikkkk.auth_service.service;

import com.dev.quikkkk.auth_service.dto.request.LoginRequest;
import com.dev.quikkkk.auth_service.dto.request.RefreshTokenRequest;
import com.dev.quikkkk.auth_service.dto.request.RegistrationRequest;
import com.dev.quikkkk.auth_service.dto.request.UpdateRoleRequest;
import com.dev.quikkkk.auth_service.dto.response.AuthenticationResponse;
import com.dev.quikkkk.auth_service.dto.response.UserResponse;
import com.dev.quikkkk.auth_service.entity.UserCredentials;

public interface IAuthenticationService {
    AuthenticationResponse login(LoginRequest request);

    AuthenticationResponse refreshToken(RefreshTokenRequest request);

    void logout(String token);

    void register(RegistrationRequest request);

    UserResponse getUserById(String id);

    UserCredentials findUserByUsername(String username);

    void updateUserRole(String userId, UpdateRoleRequest request);

    void deleteUser(String id);
}
