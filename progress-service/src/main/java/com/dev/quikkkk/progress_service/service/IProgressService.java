package com.dev.quikkkk.progress_service.service;

import com.dev.quikkkk.progress_service.dto.request.CompleteLessonRequest;
import com.dev.quikkkk.progress_service.dto.request.internal.EnrollStudentRequest;
import com.dev.quikkkk.progress_service.dto.response.CourseProgressResponse;
import com.dev.quikkkk.progress_service.dto.response.ProgressResponse;
import com.dev.quikkkk.progress_service.dto.response.StudentProgressSummary;

import java.util.List;
import java.util.Optional;

public interface IProgressService {
    ProgressResponse createProgress(EnrollStudentRequest request);

    Optional<ProgressResponse> getProgressByStudentAndCourse(String studentId, String courseId);

    List<ProgressResponse> getStudentProgress(String studentId);

    void deleteProgress(String progressId);

    void completeLesson(String studentId, String courseId, String lessonId, CompleteLessonRequest request);

    CourseProgressResponse getCourseProgress(String studentId, String courseId);

    List<StudentProgressSummary> getStudentProgressSummary(String studentId);
}
