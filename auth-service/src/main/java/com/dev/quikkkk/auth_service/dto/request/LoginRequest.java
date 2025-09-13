package com.dev.quikkkk.auth_service.dto.request;

import jakarta.validation.constraints.NotBlank;
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
public class LoginRequest {
    @NotBlank(message = "VALIDATION.LOGIN.USERNAME.NOT_BLANK")
    private String username;

    @NotBlank(message = "VALIDATION.LOGIN.PASSWORD.NOT_BLANK")
    private String password;
}
