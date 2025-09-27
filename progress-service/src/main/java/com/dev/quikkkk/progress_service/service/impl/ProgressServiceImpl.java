package com.dev.quikkkk.progress_service.service.impl;

import com.dev.quikkkk.progress_service.document.Progress;
import com.dev.quikkkk.progress_service.dto.request.internal.EnrollStudentRequest;
import com.dev.quikkkk.progress_service.dto.response.ProgressResponse;
import com.dev.quikkkk.progress_service.exception.BusinessException;
import com.dev.quikkkk.progress_service.exception.ErrorCode;
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
        log.info("Creating progress for student: {}, in course: {}", request.getStudentId(), request.getCourseId());
        Optional<Progress> existingProgress = repository
                .findByStudentIdAndCourseId(request.getStudentId(), request.getCourseId());

        if (existingProgress.isPresent()) {
            log.warn("Progress already exists for student: {} in course: {}",
                    request.getStudentId(), request.getCourseId()
            );

            return mapper.toProgressResponse(existingProgress.get());
        }

        var progress = mapper.toProgress(request);
        var savedProgress = repository.save(progress);

        log.info("Progress created with ID: {} for student: {}", savedProgress.getId(), request.getStudentId());

        return mapper.toProgressResponse(savedProgress);
    }

    @Override
    public Optional<ProgressResponse> getProgressByStudentAndCourse(String studentId, String courseId) {
        log.info("Getting progress for student: {}, in course: {}", studentId, courseId);
        return repository.findByStudentIdAndCourseId(studentId, courseId).map(mapper::toProgressResponse);
    }

    @Override
    public List<ProgressResponse> getStudentProgress(String studentId) {
        log.info("Getting progress for student: {}", studentId);

        List<Progress> progressList = repository.findByStudentId(studentId);
        log.debug("Found {} courses for student: {}", progressList.size(), studentId);
        return progressList.stream()
                .map(mapper::toProgressResponse)
                .toList();
    }

    @Override
    public void deleteProgress(String progressId) {
        log.info("Deleting progress with ID: {}", progressId);

        if (repository.existsById(progressId)) {
            repository.deleteById(progressId);
            log.info("Deleted progress with ID: {}", progressId);
        } else {
            log.warn("Progress not found for deletion: {}", progressId);
            throw new BusinessException(ErrorCode.PROGRESS_NOT_FOUND, progressId);
        }
    }
}
