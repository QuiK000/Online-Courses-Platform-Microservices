package com.dev.quikkkk.user_service.service.impl;

import com.dev.quikkkk.user_service.dto.request.CreateUserRequest;
import com.dev.quikkkk.user_service.dto.response.UserResponse;
import com.dev.quikkkk.user_service.entity.User;
import com.dev.quikkkk.user_service.mapper.UserMapper;
import com.dev.quikkkk.user_service.repository.IUserRepository;
import com.dev.quikkkk.user_service.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements IUserService {
    private final UserMapper mapper;
    private final IUserRepository repository;

    @Override
    public void createUser(CreateUserRequest request) {
        log.info("Create user request: {}", request);

        User user = mapper.toUser(request);
        repository.save(user);

        log.info("User profile created successfully: {}", user.getUsername());
    }

    @Override
    public UserResponse getUserById(String id) {
        log.info("Getting user by id: {}", id);
        return repository
                .findById(id)
                .map(mapper::toUserResponse)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public List<UserResponse> getUsersByRole(String role) {
        log.info("Getting users by role: {}", role);
        return repository
                .findByRole(role.toUpperCase())
                .stream()
                .map(mapper::toUserResponse)
                .toList();
    }

    @Override
    public void deleteUser(String id) {
        log.info("Deleting user by id: {}", id);
        repository.deleteById(id);
    }

    @Override
    public List<UserResponse> searchUsers(String name) {
        return repository
                .findByUsernameContaining(name)
                .stream()
                .map(mapper::toUserResponse)
                .toList();
    }
}
