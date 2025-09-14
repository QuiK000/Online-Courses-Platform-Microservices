package com.dev.quikkkk.user_service.mapper;

import com.dev.quikkkk.user_service.dto.request.CreateUserRequest;
import com.dev.quikkkk.user_service.dto.response.UserResponse;
import com.dev.quikkkk.user_service.entity.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserMapper {
    public User toUser(CreateUserRequest request) {
        return User
                .builder()
                .id(request.getId())
                .username(request.getUsername())
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .role(request.getRole())
                .profilePicture(null)
                .bio(null)
                .phone(null)
                .createdDate(LocalDateTime.now())
                .lastModifiedDate(LocalDateTime.now())
                .build();
    }

    public UserResponse toUserResponse(User user) {
        return UserResponse
                .builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .profilePicture(user.getProfilePicture())
                .bio(user.getBio())
                .phone(user.getPhone())
                .createdDate(user.getCreatedDate())
                .build();
    }
}
