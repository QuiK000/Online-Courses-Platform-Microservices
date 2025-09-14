package com.dev.quikkkk.user_service.controller;

import com.dev.quikkkk.user_service.dto.request.CreateUserRequest;
import com.dev.quikkkk.user_service.dto.response.ApiResponse;
import com.dev.quikkkk.user_service.dto.response.UserResponse;
import com.dev.quikkkk.user_service.security.UserPrincipal;
import com.dev.quikkkk.user_service.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final IUserService service;

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createUser(@RequestBody CreateUserRequest request) {
        service.createUser(request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@userSecurityServiceImpl.canAccessUser(#id, authentication.principal.id) or hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(service.getUserById(id)));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getMyProfile(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        UserResponse user = service.getUserById(principal.id());
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> getUsersByRole(@RequestParam String role) {
        List<UserResponse> users = service.getUsersByRole(role);
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable String id) {
        service.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<UserResponse>>> searchUsers(@RequestParam String name) {
        List<UserResponse> users = service.searchUsers(name);
        return ResponseEntity.ok(ApiResponse.success(users));
    }
}
