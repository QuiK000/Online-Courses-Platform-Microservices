package com.dev.quikkkk.progress_service.service;

import com.dev.quikkkk.progress_service.dto.request.internal.EnrollStudentRequest;
import com.dev.quikkkk.progress_service.dto.response.ProgressResponse;

import java.util.List;
import java.util.Optional;

public interface IProgressService {
    ProgressResponse createProgress(EnrollStudentRequest request);

    Optional<ProgressResponse> getProgressByStudentAndCourse(String studentId, String courseId);

    List<ProgressResponse> getStudentProgress(String studentId);


    void deleteProgress(String progressId);
}
