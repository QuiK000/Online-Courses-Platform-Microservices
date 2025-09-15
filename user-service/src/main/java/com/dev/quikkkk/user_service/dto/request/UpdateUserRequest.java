package com.dev.quikkkk.user_service.dto.request;

import com.dev.quikkkk.user_service.validation.NonDisposableEmail;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateUserRequest {
    @Size(min = 3, max = 50, message = "VALIDATION.UPDATE_USER.USERNAME.SIZE")
    private String username;

    @Email(message = "VALIDATION.UPDATE_USER.EMAIL.FORMAT")
    @NonDisposableEmail(message = "VALIDATION.UPDATE_USER.EMAIL.DISPOSABLE")
    private String email;

    @Size(min = 3, max = 50, message = "VALIDATION.UPDATE_USER.FIRST_NAME.SIZE")
    private String firstName;

    @Size(min = 3, max = 50, message = "VALIDATION.UPDATE_USER.LAST_NAME.SIZE")
    private String lastName;

    @Pattern(regexp = "^(male|family)?$", message = "VALIDATION.UPDATE_USER.BIO.INVALID")
    private String bio;

    @Pattern(regexp = "^(\\+\\d{1,3}[- ]?)?\\(?\\d{3}\\)?[- ]?\\d{3}[- ]?\\d{4}$", message = "VALIDATION.UPDATE_USER.PHONE.BAD.VALIDATION")
    private String phone;

    @URL(message = "VALIDATION.UPDATE_USER.IMAGE_URL.FORMAT")
    private String profilePicture;
}
