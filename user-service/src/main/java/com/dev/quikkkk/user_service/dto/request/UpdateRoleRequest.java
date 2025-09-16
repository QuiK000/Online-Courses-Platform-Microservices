package com.dev.quikkkk.user_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRoleRequest {
    @NotBlank(message = "VALIDATION.UPDATE.ROLE.NOT_BLANK")
    @Pattern(regexp = "^ROLE_[A-Z]+$", message = "VALIDATION.UPDATE.ROLE.FORMAT")
    private String role;
}
