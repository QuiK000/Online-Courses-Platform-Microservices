package com.dev.quikkkk.progress_service.controller;

import com.dev.quikkkk.progress_service.dto.request.internal.EnrollStudentRequest;
import com.dev.quikkkk.progress_service.dto.response.ApiResponse;
import com.dev.quikkkk.progress_service.dto.response.ProgressResponse;
import com.dev.quikkkk.progress_service.service.IProgressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/internal/progress")
@RequiredArgsConstructor
public class InternalProgressController {
    private final IProgressService service;

    @PostMapping("/enroll")
    public ApiResponse<ProgressResponse> enrollStudent(@Valid @RequestBody EnrollStudentRequest request) {
        ProgressResponse response = service.createProgress(request);
        return ApiResponse.success(response);
    }

    @GetMapping("/student/{student-id}/course/{course-id}")
    public ApiResponse<ProgressResponse> getProgress(
            @PathVariable("student-id") String studentId,
            @PathVariable("course-id") String courseId
    ) {
        Optional<ProgressResponse> progress = service.getProgressByStudentAndCourse(studentId, courseId);
        return progress.map(ApiResponse::success).orElseGet(() -> ApiResponse.error("Progress not found"));
    }
}
