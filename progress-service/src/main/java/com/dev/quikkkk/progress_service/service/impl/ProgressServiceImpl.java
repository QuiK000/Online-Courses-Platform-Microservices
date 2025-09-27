package com.dev.quikkkk.progress_service.service.impl;

import com.dev.quikkkk.progress_service.dto.request.internal.EnrollStudentRequest;
import com.dev.quikkkk.progress_service.dto.response.ProgressResponse;
import com.dev.quikkkk.progress_service.mapper.ProgressMapper;
import com.dev.quikkkk.progress_service.repository.IProgressRepository;
import com.dev.quikkkk.progress_service.service.IProgressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProgressServiceImpl implements IProgressService {
    private final IProgressRepository repository;
    private final ProgressMapper mapper;

    @Override
    public ProgressResponse createProgress(EnrollStudentRequest request) {
        var progressResponse = mapper.toProgressResponse(request);
        repository.save(mapper.toProgress(progressResponse));

        return progressResponse;
    }

    @Override
    public Optional<ProgressResponse> getProgressByStudentAndCourse(String studentId, String courseId) {
        return Optional.empty();
    }

    @Override
    public List<ProgressResponse> getStudentProgress(String studentId) {
        return List.of();
    }

    @Override
    public void deleteProgress(String progressId) {

    }
}
