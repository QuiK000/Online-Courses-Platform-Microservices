package com.dev.quikkkk.user_service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateUserRequest {
    private String id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String role;
}
