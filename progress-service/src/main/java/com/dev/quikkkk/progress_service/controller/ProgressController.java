package com.dev.quikkkk.progress_service.controller;

import com.dev.quikkkk.progress_service.dto.request.CompleteLessonRequest;
import com.dev.quikkkk.progress_service.dto.response.ApiResponse;
import com.dev.quikkkk.progress_service.dto.response.CourseProgressResponse;
import com.dev.quikkkk.progress_service.dto.response.StudentProgressSummary;
import com.dev.quikkkk.progress_service.security.UserPrincipal;
import com.dev.quikkkk.progress_service.service.IProgressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/progress")
@RequiredArgsConstructor
@Slf4j
public class ProgressController {
    private final IProgressService service;

    @PostMapping("/courses/{course-id}/lessons/{lesson-id}/complete")
    public ResponseEntity<ApiResponse<Void>> completeLesson(
            @PathVariable("course-id") String courseId,
            @PathVariable("lesson-id") String lessonId,
            @RequestBody @Valid CompleteLessonRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        log.info("Completing lesson {} for student {}", lessonId, principal.id());
        service.completeLesson(principal.id(), courseId, lessonId, request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/courses/{course-id}")
    public ResponseEntity<ApiResponse<CourseProgressResponse>> getCourseProgress(
            @PathVariable("course-id") String courseId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        CourseProgressResponse progress = service.getCourseProgress(principal.id(), courseId);
        return ResponseEntity.ok(ApiResponse.success(progress));
    }

    @GetMapping("/student")
    public ResponseEntity<ApiResponse<List<StudentProgressSummary>>> getStudentProgress(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        List<StudentProgressSummary> progress = service.getStudentProgressSummary(principal.id());
        return ResponseEntity.ok(ApiResponse.success(progress));
    }
}
