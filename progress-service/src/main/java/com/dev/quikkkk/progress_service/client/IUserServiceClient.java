package com.dev.quikkkk.progress_service.client;

import com.dev.quikkkk.progress_service.dto.response.ApiResponse;
import com.dev.quikkkk.progress_service.dto.response.UserInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "${app.config.user-service-url:http://localhost:8222}")
public interface IUserServiceClient {
    @GetMapping("/internal/users/{id}")
    ApiResponse<UserInfo> getUserById(@PathVariable String id);
}
