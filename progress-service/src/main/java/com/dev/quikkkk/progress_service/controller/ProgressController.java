package com.dev.quikkkk.progress_service.controller;

import com.dev.quikkkk.progress_service.dto.response.ApiResponse;
import com.dev.quikkkk.progress_service.dto.response.CourseProgressResponse;
import com.dev.quikkkk.progress_service.dto.response.StudentProgressSummary;
import com.dev.quikkkk.progress_service.security.UserPrincipal;
import com.dev.quikkkk.progress_service.service.IProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/progress")
@RequiredArgsConstructor
public class ProgressController {
    private final IProgressService service;

    @GetMapping("/courses/{course-id}")
    public ResponseEntity<ApiResponse<CourseProgressResponse>> getCourseProgress(
            @PathVariable("course-id") String courseId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return null;
    }

    @GetMapping("/student")
    public ResponseEntity<ApiResponse<List<StudentProgressSummary>>> getStudentProgress(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return null;
    }
}
