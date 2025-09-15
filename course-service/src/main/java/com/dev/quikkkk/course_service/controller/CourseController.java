package com.dev.quikkkk.course_service.controller;

import com.dev.quikkkk.course_service.dto.request.CreateCourseRequest;
import com.dev.quikkkk.course_service.dto.response.ApiResponse;
import com.dev.quikkkk.course_service.dto.response.CourseResponse;
import com.dev.quikkkk.course_service.security.UserPrincipal;
import com.dev.quikkkk.course_service.service.ICourseService;
import jakarta.validation.Valid;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
public class CourseController {
    private final ICourseService service;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public ResponseEntity<ApiResponse<Void>> createCourse(
            @RequestBody @Valid CreateCourseRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        var teacherId = principal.id();
        service.createCourse(request, teacherId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CourseResponse>>> getAllCourses() {
        return ResponseEntity.ok(ApiResponse.success(service.getAllCourses()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCourse(@PathVariable String id) {
        service.deleteCourse(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
