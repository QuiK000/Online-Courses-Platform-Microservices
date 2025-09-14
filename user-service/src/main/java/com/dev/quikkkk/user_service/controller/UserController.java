package com.dev.quikkkk.user_service.controller;

import com.dev.quikkkk.user_service.dto.request.UpdateUserRequest;
import com.dev.quikkkk.user_service.dto.response.ApiResponse;
import com.dev.quikkkk.user_service.dto.response.UserResponse;
import com.dev.quikkkk.user_service.security.UserPrincipal;
import com.dev.quikkkk.user_service.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "API for management user")
public class UserController {
    private final IUserService service;

    @GetMapping("/{id}")
    @Operation(
            summary = "Get user by id",
            description = "Return information about the user by their id"
    )
    @PreAuthorize("@userSecurityServiceImpl.canAccessUser(#id, authentication.principal.id) or hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(service.getUserById(id)));
    }

    @GetMapping("/me")
    @Operation(
            summary = "Get current user",
            description = "Return information about authorization user",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden"),
    })
    public ResponseEntity<ApiResponse<UserResponse>> getMyProfile(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        UserResponse user = service.getUserById(principal.id());
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    @GetMapping
    @Operation(summary = "Get users by role")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getUsersByRole(@RequestParam String role) {
        List<UserResponse> users = service.getUsersByRole(role);
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @PatchMapping
    @Operation(summary = "Update user profile")
    @PreAuthorize("@userSecurityServiceImpl.canEditUser(#principal.id, authentication.principal.id) or hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> updateUserProfile(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody @Valid UpdateUserRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(service.updateUser(principal.id(), request)));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete user",
            description = "Delete user by their id",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable String id) {
        service.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/search")
    @Operation(summary = "Search users by name")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TEACHER')")
    public ResponseEntity<ApiResponse<List<UserResponse>>> searchUsers(@RequestParam String name) {
        List<UserResponse> users = service.searchUsers(name);
        return ResponseEntity.ok(ApiResponse.success(users));
    }
}
