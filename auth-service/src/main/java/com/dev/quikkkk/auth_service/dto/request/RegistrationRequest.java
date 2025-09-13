package com.dev.quikkkk.auth_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
public class RegistrationRequest {
    @NotBlank(message = "VALIDATION.REGISTRATION.USERNAME.NOT_BLANK")
    @Size(min = 3, max = 50, message = "VALIDATION.REGISTRATION.USERNAME.SIZE")
    private String username;

    @NotBlank(message = "VALIDATION.REGISTRATION.EMAIL.NOT_BLANK")
    @Size(max = 50, message = "VALIDATION.REGISTRATION.EMAIL.SIZE")
    private String email;

    @NotBlank(message = "VALIDATION.REGISTRATION.FIRST_NAME.NOT_BLANK")
    @Size(min = 3, max = 50, message = "VALIDATION.REGISTRATION.FIRST_NAME.SIZE")
    private String firstName;

    @NotBlank(message = "VALIDATION.REGISTRATION.LAST_NAME.NOT_BLANK")
    @Size(min = 3, max = 50, message = "VALIDATION.REGISTRATION.LAST_NAME.SIZE")
    private String lastName;

    @NotBlank(message = "VALIDATION.REGISTRATION.PASSWORD.NOT_BLANK")
    @Size(min = 8, max = 50, message = "VALIDATION.REGISTRATION.PASSWORD.SIZE")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*\\W).*$", message = "VALIDATION.REGISTRATION.PASSWORD.WEAK")
    private String password;

    @NotBlank(message = "VALIDATION.REGISTRATION.CONFIRM_PASSWORD.NOT_BLANK")
    @Size(min = 8, max = 50, message = "VALIDATION.REGISTRATION.CONFIRM_PASSWORD.SIZE")
    private String confirmPassword;
}
