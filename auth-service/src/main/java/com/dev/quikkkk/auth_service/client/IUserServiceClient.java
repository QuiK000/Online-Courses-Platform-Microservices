package com.dev.quikkkk.auth_service.client;

import com.dev.quikkkk.auth_service.dto.request.CreateUserRequest;
import com.dev.quikkkk.auth_service.dto.request.UpdateRoleRequest;
import com.dev.quikkkk.auth_service.dto.response.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "user-service", url = "${app.config.user-service-url}")
public interface IUserServiceClient {
    @PostMapping
    void createUser(@RequestBody CreateUserRequest request);

    @GetMapping("/{id}")
    UserResponse getUserById(@PathVariable String id);

    @PutMapping("/{userId}/role")
    ResponseEntity<Void> updateUserRole(@PathVariable String userId, @RequestBody UpdateRoleRequest request);

    @DeleteMapping("/{id}")
    void deleteUser(@PathVariable String id);
}
