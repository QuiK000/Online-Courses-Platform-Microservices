package com.dev.quikkkk.progress_service.service.impl;

import com.dev.quikkkk.progress_service.client.IUserServiceClient;
import com.dev.quikkkk.progress_service.document.LessonProgress;
import com.dev.quikkkk.progress_service.document.Progress;
import com.dev.quikkkk.progress_service.dto.kafka.LessonCompletedEvent;
import com.dev.quikkkk.progress_service.dto.kafka.ProgressMilestoneEvent;
import com.dev.quikkkk.progress_service.dto.request.CompleteLessonRequest;
import com.dev.quikkkk.progress_service.dto.request.internal.EnrollStudentRequest;
import com.dev.quikkkk.progress_service.dto.response.CourseProgressResponse;
import com.dev.quikkkk.progress_service.dto.response.ProgressResponse;
import com.dev.quikkkk.progress_service.dto.response.StudentProgressSummary;
import com.dev.quikkkk.progress_service.dto.response.UserInfo;
import com.dev.quikkkk.progress_service.enums.CourseStatus;
import com.dev.quikkkk.progress_service.exception.BusinessException;
import com.dev.quikkkk.progress_service.mapper.ProgressMapper;
import com.dev.quikkkk.progress_service.repository.IProgressRepository;
import com.dev.quikkkk.progress_service.service.IProgressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.dev.quikkkk.progress_service.enums.LessonStatus.COMPLETED;
import static com.dev.quikkkk.progress_service.enums.LessonStatus.IN_PROGRESS;
import static com.dev.quikkkk.progress_service.exception.ErrorCode.LESSON_NOT_FOUND_IN_PROGRESS;
import static com.dev.quikkkk.progress_service.exception.ErrorCode.PROGRESS_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProgressServiceImpl implements IProgressService {
    private final IProgressRepository repository;
    private final ProgressMapper mapper;
    private final IUserServiceClient client;
    private final KafkaTemplate<String, Object> kafkaTemplate;

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
            throw new BusinessException(PROGRESS_NOT_FOUND, progressId);
        }
    }

    @Override
    public void completeLesson(String studentId, String courseId, String lessonId, CompleteLessonRequest request) {
        log.info("Completing lesson {} for student {} in course {}", lessonId, studentId, courseId);

        Progress progress = repository.findByStudentIdAndCourseId(studentId, courseId)
                .orElseThrow(() -> new BusinessException(PROGRESS_NOT_FOUND, studentId));

        LessonProgress lessonProgress = findLessonProgress(progress, lessonId);

        lessonProgress.setStatus(COMPLETED);
        lessonProgress.setCompletedAt(request.getCompletedTime());
        lessonProgress.setLastAccessedAt(LocalDateTime.now());
        lessonProgress.setTimeSpentMinutes(
                (lessonProgress.getTimeSpentMinutes() != null ? lessonProgress.getTimeSpentMinutes() : 0)
                        + request.getTimeSpentMinutes()
        );
        lessonProgress.setWatchPercentage(request.getWatchPercentage());

        if (request.getNotes() != null && !request.getNotes().trim().isEmpty())
            lessonProgress.setStudentNotes(request.getNotes());

        updateProgressStats(progress);

        Progress savedProgress = repository.save(progress);

        sendLessonCompletedEvent(savedProgress, lessonProgress);
        checkAndSendMilestoneEvents(savedProgress);

        log.info("Lesson {} completed successfully for student {}", lessonId, studentId);
    }

    @Override
    public CourseProgressResponse getCourseProgress(String studentId, String courseId) {
        Progress progress = repository.findByStudentIdAndCourseId(studentId, courseId)
                .orElseThrow(() -> new BusinessException(PROGRESS_NOT_FOUND, studentId));
        return mapper.toCourseProgressResponse(progress);
    }

    @Override
    public List<StudentProgressSummary> getStudentProgressSummary(String studentId) {
        List<Progress> progressList = repository.findByStudentId(studentId);
        return progressList.stream()
                .map(mapper::toProgressSummary)
                .toList();
    }

    private LessonProgress findLessonProgress(Progress progress, String lessonId) {
        return progress.getLessons().stream()
                .filter(lesson -> lesson.getLessonId().equals(lessonId))
                .findFirst()
                .orElseThrow(() -> new BusinessException(LESSON_NOT_FOUND_IN_PROGRESS, lessonId));
    }

    private void updateProgressStats(Progress progress) {
        List<LessonProgress> lessons = progress.getLessons();
        int totalLessons = lessons.size();
        int completedLessons = (int) lessons.stream()
                .filter(l -> l.getStatus() == COMPLETED)
                .count();
        int inProgressLessons = (int) lessons.stream()
                .filter(l -> l.getStatus() == IN_PROGRESS)
                .count();

        double completionPercentage = totalLessons > 0 ? (double) completedLessons / totalLessons * 100 : 0;

        progress.getStats().setTotalLessons(totalLessons);
        progress.getStats().setCompletedLessons(completedLessons);
        progress.getStats().setInProgressLessons(inProgressLessons);
        progress.getStats().setNotStartedLessons(totalLessons - completedLessons - inProgressLessons);
        progress.getStats().setCompletionPercentage(completionPercentage);

        int totalTimeSpent = lessons.stream()
                .mapToInt(l -> l.getTimeSpentMinutes() != null ? l.getTimeSpentMinutes() : 0)
                .sum();

        progress.getStats().setTotalTimeSpentMinutes(totalTimeSpent);
        progress.getStats().setAverageTimePerLesson(
                completedLessons > 0 ? totalTimeSpent / completedLessons : 0
        );

        if (completionPercentage >= 100.0) {
            progress.setCourseStatus(CourseStatus.COMPLETED);
            progress.setCompletedAt(LocalDateTime.now());
        } else if (completedLessons > 0) {
            progress.setCourseStatus(CourseStatus.IN_PROGRESS);
        }

        progress.setLastActivityAt(LocalDateTime.now());
    }

    private void sendLessonCompletedEvent(Progress progress, LessonProgress lessonProgress) {
        try {
            UserInfo userInfo = getUserInfo(progress.getStudentId());
            LessonCompletedEvent event = LessonCompletedEvent.builder()
                    .studentId(progress.getStudentId())
                    .studentEmail(userInfo.getEmail())
                    .studentName(userInfo.getFirstName() + " " + userInfo.getLastName())
                    .courseId(progress.getCourseId())
                    .courseName("Learning course") // TODO: GET FROM COURSE SERVICE
                    .lessonId(lessonProgress.getLessonId())
                    .lessonTitle(lessonProgress.getLessonTitle())
                    .timeSpentMinutes(lessonProgress.getTimeSpentMinutes())
                    .completedAt(lessonProgress.getCompletedAt())
                    .build();

            kafkaTemplate.send("lesson-completed-topic", event);
            log.info("Lesson completed event sent for student {}", progress.getStudentId());
        } catch (Exception e) {
            log.error("Failed to send lesson completed event: {}", e.getMessage());
        }
    }

    private void checkAndSendMilestoneEvents(Progress progress) {
        Double completionPercentage = progress.getStats().getCompletionPercentage();
        List<Double> milestones = List.of(25.0, 50.0, 75.0, 100.0);

        for (Double milestone : milestones) {
            String milestoneKey = milestone == 100.0 ? "COMPLETED" : String.format("%.0f%%", milestone);

            if (completionPercentage >= milestone && !progress.getSentMilestones().contains(milestoneKey)) {
                sendMilestoneEvent(progress, milestone, milestoneKey);
                progress.getSentMilestones().add(milestoneKey);
                repository.save(progress); // Update sent milestones
            }
        }
    }

    private void sendMilestoneEvent(Progress progress, Double milestone, String milestoneKey) {
        try {
            UserInfo userInfo = getUserInfo(progress.getStudentId());

            ProgressMilestoneEvent event = ProgressMilestoneEvent.builder()
                    .studentId(progress.getStudentId())
                    .studentEmail(userInfo.getEmail())
                    .studentName(userInfo.getFirstName() + " " + userInfo.getLastName())
                    .courseId(progress.getCourseId())
                    .courseName("Learning Course") // TODO: Get from course service
                    .completionPercentage(milestone)
                    .milestone(milestoneKey)
                    .completedLessons(progress.getStats().getCompletedLessons())
                    .totalLessons(progress.getStats().getTotalLessons())
                    .achievedAt(LocalDateTime.now())
                    .build();

            kafkaTemplate.send("progress-milestone-topic", event);
            log.info("Milestone {}% event sent for student {}", milestone,  progress.getStudentId());
        } catch (Exception e) {
            log.error("Failed to sent milestone event: {}", e.getMessage());
        }
    }

    private UserInfo getUserInfo(String userId) {
        try {
            return client.getUserById(userId).data();
        } catch (Exception e) {
            log.error("Failed to get user info for user {}: {}", userId, e.getMessage());
            return UserInfo.builder()
                    .id(userId)
                    .email("unknown@example.com")
                    .firstName("Unknown")
                    .lastName("Unknown")
                    .build();
        }
    }
}
