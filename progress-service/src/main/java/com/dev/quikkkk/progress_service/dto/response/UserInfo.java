package com.dev.quikkkk.progress_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class UserInfo {
    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private String username;
    private String role;
}
