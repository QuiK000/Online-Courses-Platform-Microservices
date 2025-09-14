package com.dev.quikkkk.user_service.service.impl;

import com.dev.quikkkk.user_service.dto.request.CreateUserRequest;
import com.dev.quikkkk.user_service.dto.request.UpdateUserRequest;
import com.dev.quikkkk.user_service.dto.response.UserResponse;
import com.dev.quikkkk.user_service.entity.User;
import com.dev.quikkkk.user_service.exception.BusinessException;
import com.dev.quikkkk.user_service.exception.ErrorCode;
import com.dev.quikkkk.user_service.mapper.UserMapper;
import com.dev.quikkkk.user_service.repository.IUserRepository;
import com.dev.quikkkk.user_service.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements IUserService {
    private final UserMapper mapper;
    private final IUserRepository repository;

    @Override
    @CacheEvict(value = {
            "userById", "usersByRole", "userSearch"
    }, allEntries = true)
    public void createUser(CreateUserRequest request) {
        log.info("Create user request: {}", request);

        User user = mapper.toUser(request);
        repository.save(user);

        log.info("User profile created successfully: {}", user.getUsername());
    }

    @Override
    @Cacheable(value = "userById", key = "#id")
    public UserResponse getUserById(String id) {
        log.info("Getting user by id: {}", id);
        return repository
                .findById(id)
                .map(mapper::toUserResponse)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    @Override
    @Cacheable(value = "usersByRole", key = "#role")
    public List<UserResponse> getUsersByRole(String role) {
        log.info("Getting users by role: {}", role);
        return repository
                .findByRole(role.toUpperCase())
                .stream()
                .map(mapper::toUserResponse)
                .toList();
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "userById", key = "#id"),
            @CacheEvict(value = "usersByRole", allEntries = true),
            @CacheEvict(value = "userSearch", allEntries = true)
    })
    public UserResponse updateUser(String id, UpdateUserRequest request) {
        log.info("Updating user by id: {}", id);

        User user = repository.findById(id).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        mapper.mergeUser(user, request);

        User updatedUser = repository.save(user);
        return mapper.toUserResponse(updatedUser);
    }

    @Override
    @CacheEvict(value = {
            "userById", "usersByRole", "userSearch"
    }, allEntries = true)
    public void deleteUser(String id) {
        log.info("Deleting user by id: {}", id);
        repository.deleteById(id);
    }

    @Override
    @Cacheable(value = "userSearch", key = "#name")
    public List<UserResponse> searchUsers(String name) {
        log.info("Searching users by name: {}", name);
        return repository
                .findByUsernameContaining(name)
                .stream()
                .map(mapper::toUserResponse)
                .toList();
    }
}
