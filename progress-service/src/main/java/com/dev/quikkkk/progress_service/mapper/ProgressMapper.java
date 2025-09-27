package com.dev.quikkkk.progress_service.mapper;

import com.dev.quikkkk.progress_service.document.Progress;
import com.dev.quikkkk.progress_service.dto.request.internal.EnrollStudentRequest;
import com.dev.quikkkk.progress_service.dto.response.ProgressResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ProgressMapper {

    public ProgressResponse toProgressResponse(EnrollStudentRequest request) {
        return ProgressResponse
                .builder()
                .studentId(request.getStudentId())
                .courseId(request.getCourseId())
                .teacherId(request.getTeacherId())
                .lessons(request.getLessons())
                .enrolledAt(LocalDateTime.now())
                .build();
    }

    public Progress toProgress(ProgressResponse progressResponse) {
        return Progress
                .builder()
                .id(progressResponse.getId())
                .studentId(progressResponse.getStudentId())
                .courseId(progressResponse.getCourseId())
                .teacherId(progressResponse.getTeacherId())
                .enrolledAt(progressResponse.getEnrolledAt())
                .build();
    }
}
