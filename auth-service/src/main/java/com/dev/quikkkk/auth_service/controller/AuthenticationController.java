package com.dev.quikkkk.auth_service.controller;

import com.dev.quikkkk.auth_service.dto.request.LoginRequest;
import com.dev.quikkkk.auth_service.dto.request.RefreshTokenRequest;
import com.dev.quikkkk.auth_service.dto.request.RegistrationRequest;
import com.dev.quikkkk.auth_service.dto.request.ResendVerificationRequest;
import com.dev.quikkkk.auth_service.dto.request.UpdateRoleRequest;
import com.dev.quikkkk.auth_service.dto.request.VerifyEmailRequest;
import com.dev.quikkkk.auth_service.dto.response.ApiResponse;
import com.dev.quikkkk.auth_service.dto.response.AuthenticationResponse;
import com.dev.quikkkk.auth_service.dto.response.UserResponse;
import com.dev.quikkkk.auth_service.security.UserPrincipal;
import com.dev.quikkkk.auth_service.service.IAuthenticationService;
import com.dev.quikkkk.auth_service.service.IEmailVerificationService;
import com.dev.quikkkk.auth_service.utils.NetworkUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final IAuthenticationService authenticationService;
    private final IEmailVerificationService emailVerificationService;


    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody RegistrationRequest request) {
        authenticationService.register(request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.success(authenticationService.login(request)));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> refreshToken(@RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(ApiResponse.success(authenticationService.refreshToken(request)));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer "))
            return ResponseEntity.badRequest().body(ApiResponse.error("Authorization header is missing or invalid"));

        String token = authHeader.substring(7);
        authenticationService.logout(token);

        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/verify-email")
    public ResponseEntity<ApiResponse<Void>> verifyEmail(
            @Valid @RequestBody VerifyEmailRequest request
    ) {
        emailVerificationService.verifyEmail(request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<ApiResponse<Void>> resendVerification(
            @Valid @RequestBody ResendVerificationRequest request,
            HttpServletRequest httpRequest
    ) {
        String ipAddress = NetworkUtils.getClientIp(httpRequest);
        emailVerificationService.resendVerificationCode(request, ipAddress);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        checkUserPrincipal(principal);
        return ResponseEntity.ok(ApiResponse.success(authenticationService.getUserById(principal.id())));
    }

    @GetMapping("/verification-status")
    public ResponseEntity<ApiResponse<Boolean>> getVerificationStatus(
            @RequestParam String email
    ) {
        boolean isVerified = emailVerificationService.isEmailRecentlyVerified(email);
        return ResponseEntity.ok(ApiResponse.success(isVerified));
    }

    @PutMapping("/users/{userId}/role")
    public ResponseEntity<Void> updateUserRole(
            @PathVariable String userId,
            @RequestBody UpdateRoleRequest request
    ) {
        authenticationService.updateUserRole(userId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        checkUserPrincipal(principal);
        authenticationService.deleteUser(principal.id());
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    private void checkUserPrincipal(UserPrincipal principal) {
        if (principal == null) ResponseEntity.badRequest().body(ApiResponse.error("User not found"));
    }
}
