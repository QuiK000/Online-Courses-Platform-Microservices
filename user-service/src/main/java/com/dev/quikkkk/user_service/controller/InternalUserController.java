package com.dev.quikkkk.user_service.controller;

import com.dev.quikkkk.user_service.dto.request.CreateUserRequest;
import com.dev.quikkkk.user_service.dto.response.ApiResponse;
import com.dev.quikkkk.user_service.dto.response.UserResponse;
import com.dev.quikkkk.user_service.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/users")
@RequiredArgsConstructor
@Slf4j
public class InternalUserController {
    private final IUserService service;

    @PostMapping
    public ApiResponse<Void> createUser(@RequestBody CreateUserRequest request) {
        log.info("Internal request to create user: {}", request);
        service.createUser(request);
        return ApiResponse.success(null);
    }

    @GetMapping("/{id}")
    public ApiResponse<UserResponse> getUserById(@PathVariable String id) {
        log.info("Internal request to get user by id: {}", id);
        return ApiResponse.success(service.getUserById(id));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteUser(@PathVariable String id) {
        log.info("Internal request to delete user by id: {}", id);
        service.deleteUser(id);
        return ApiResponse.success(null);
    }
}
