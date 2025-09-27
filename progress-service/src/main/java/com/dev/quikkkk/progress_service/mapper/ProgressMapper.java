package com.dev.quikkkk.progress_service.mapper;

import com.dev.quikkkk.progress_service.dto.request.internal.EnrollStudentRequest;
import com.dev.quikkkk.progress_service.dto.response.ProgressResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ProgressMapper {

    public ProgressResponse toProgress(EnrollStudentRequest request) {
        return ProgressResponse
                .builder()
                .studentId(request.getStudentId())
                .courseId(request.getCourseId())
                .enrolledAt(LocalDateTime.now())
                .build();
    }
}
