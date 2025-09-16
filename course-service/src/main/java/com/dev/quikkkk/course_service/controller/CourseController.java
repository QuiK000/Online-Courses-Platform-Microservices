package com.dev.quikkkk.course_service.controller;

import com.dev.quikkkk.course_service.dto.request.CreateCourseRequest;
import com.dev.quikkkk.course_service.dto.request.CreateLessonRequest;
import com.dev.quikkkk.course_service.dto.response.ApiResponse;
import com.dev.quikkkk.course_service.dto.response.CourseResponse;
import com.dev.quikkkk.course_service.dto.response.CoursesAndLessonsResponse;
import com.dev.quikkkk.course_service.dto.response.LessonResponse;
import com.dev.quikkkk.course_service.security.UserPrincipal;
import com.dev.quikkkk.course_service.service.ICourseService;
import com.dev.quikkkk.course_service.service.ILessonService;
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
    private final ICourseService courseService;
    private final ILessonService lessonService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public ResponseEntity<ApiResponse<Void>> createCourse(
            @RequestBody @Valid CreateCourseRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        var teacherId = principal.id();
        courseService.createCourse(request, teacherId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/{id}/lessons")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public ResponseEntity<ApiResponse<LessonResponse>> createLesson(
            @RequestBody @Valid CreateLessonRequest request,
            @PathVariable String id
    ) {
        return ResponseEntity.ok(ApiResponse.success(lessonService.saveLesson(id, request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CourseResponse>>> getAllCourses() {
        return ResponseEntity.ok(ApiResponse.success(courseService.getAllCourses()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CoursesAndLessonsResponse>> getCourseWithLessons(
            @PathVariable String id
    ) {
        return ResponseEntity.ok(ApiResponse.success(courseService.getCourseWithLessons(id)));
    }

    @GetMapping("/{courseId}/lessons")
    public ResponseEntity<ApiResponse<List<LessonResponse>>> getLessonsByCourseId(@PathVariable String courseId) {
        return ResponseEntity.ok(ApiResponse.success(lessonService.getLessonsByCourseId(courseId)));
    }

    @GetMapping("/lessons/{lessonId}")
    public ResponseEntity<ApiResponse<LessonResponse>> getLessonById(@PathVariable String lessonId) {
        return ResponseEntity.ok(ApiResponse.success(lessonService.getLessonById(lessonId)));
    }

    @DeleteMapping("/{courseId}/lessons/{lessonId}")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public ResponseEntity<ApiResponse<Void>> deleteLesson(@PathVariable String courseId, @PathVariable String lessonId) {
        lessonService.deleteLesson(lessonId, courseId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCourse(@PathVariable String id) {
        courseService.deleteCourse(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
