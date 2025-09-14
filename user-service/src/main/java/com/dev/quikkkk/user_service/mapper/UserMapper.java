package com.dev.quikkkk.user_service.mapper;

import com.dev.quikkkk.user_service.dto.request.CreateUserRequest;
import com.dev.quikkkk.user_service.dto.request.UpdateUserRequest;
import com.dev.quikkkk.user_service.dto.response.UserResponse;
import com.dev.quikkkk.user_service.entity.User;
import com.dev.quikkkk.user_service.exception.BusinessException;
import com.dev.quikkkk.user_service.exception.ErrorCode;
import org.apache.commons.lang3.StringUtils;
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

    public void mergeUser(User user, UpdateUserRequest request) {
        if (StringUtils.isNotBlank(request.getUsername()) && !request.getUsername().equals(user.getUsername()))
            user.setUsername(request.getUsername());

        if (StringUtils.isNotBlank(request.getEmail()) && !request.getEmail().equals(user.getEmail()))
            user.setEmail(request.getEmail());

        if (StringUtils.isNotBlank(request.getFirstName()) && !request.getFirstName().equals(user.getFirstName()))
            user.setFirstName(request.getFirstName());

        if (StringUtils.isNotBlank(request.getLastName()) && !request.getLastName().equals(user.getLastName()))
            user.setLastName(request.getLastName());

        if (request.getBio() != null) {
            if (request.getBio().isEmpty()) {
                user.setBio(null);
            } else {
                if (!request.getBio().equalsIgnoreCase("male") && !request.getBio().equalsIgnoreCase("family")) {
                    throw new BusinessException(ErrorCode.INVALID_BIO_VALUE);
                }
                user.setBio(request.getBio().toLowerCase());
            }
        }

        if (StringUtils.isNotBlank(request.getPhone()) && !request.getPhone().equals(user.getPhone()))
            user.setPhone(request.getPhone());

        if (request.getProfilePicture() != null && !request.getProfilePicture().equals(user.getProfilePicture()))
            user.setProfilePicture(request.getProfilePicture());
    }
}
