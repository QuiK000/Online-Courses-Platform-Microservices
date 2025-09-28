package com.dev.quikkkk.progress_service.mapper;

import com.dev.quikkkk.progress_service.document.LessonProgress;
import com.dev.quikkkk.progress_service.document.Progress;
import com.dev.quikkkk.progress_service.document.ProgressStats;
import com.dev.quikkkk.progress_service.dto.kafka.LessonInfo;
import com.dev.quikkkk.progress_service.dto.request.internal.EnrollStudentRequest;
import com.dev.quikkkk.progress_service.dto.response.CourseProgressResponse;
import com.dev.quikkkk.progress_service.dto.response.LessonProgressResponse;
import com.dev.quikkkk.progress_service.dto.response.ProgressResponse;
import com.dev.quikkkk.progress_service.enums.CourseStatus;
import com.dev.quikkkk.progress_service.enums.LessonStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProgressMapper {
    public ProgressResponse toProgressResponse(Progress progress) {
        return ProgressResponse
                .builder()
                .id(progress.getId())
                .studentId(progress.getStudentId())
                .courseId(progress.getCourseId())
                .teacherId(progress.getTeacherId())
                .enrolledAt(progress.getEnrolledAt())
                .build();
    }

    public CourseProgressResponse toCourseProgressResponse(Progress progress) {
        return CourseProgressResponse.builder()
                .courseId(progress.getCourseId())
                .teacherId(progress.getTeacherId())
                .courseStatus(progress.getCourseStatus())
                .lessons(progress.getLessons().stream()
                        .map(this::toLessonProgressResponse)
                        .toList())
                .enrolledAt(progress.getEnrolledAt())
                .lastActivityAt(progress.getLastActivityAt())
                .completedAt(progress.getCompletedAt())
                .build();
    }

    public Progress toProgress(EnrollStudentRequest request) {
        return Progress.builder()
                .studentId(request.getStudentId())
                .courseId(request.getCourseId())
                .teacherId(request.getTeacherId())
                .courseStatus(CourseStatus.ENROLLED)
                .lessons(createInitialLessons(request.getLessons()))
                .stats(createInitialStats(request.getLessons()))
                .enrolledAt(LocalDateTime.now())
                .lastActivityAt(LocalDateTime.now())
                .version(1)
                .build();
    }

    public LessonProgressResponse toLessonProgressResponse(LessonProgress lessonProgress) {
        return LessonProgressResponse.builder()
                .lessonId(lessonProgress.getLessonId())
                .lessonOrder(lessonProgress.getLessonOrder())
                .status(lessonProgress.getStatus())
                .startedAt(lessonProgress.getStartedAt())
                .completedAt(lessonProgress.getCompletedAt())
                .lastAccessedAt(lessonProgress.getLastAccessedAt())
                .timeSpentMinutes(lessonProgress.getTimeSpentMinutes())
                .watchPercentage(lessonProgress.getWatchPercentage())
                .studentNotes(lessonProgress.getStudentNotes())
                .attemptCount(lessonProgress.getAttemptCount())
                .build();
    }

    private List<LessonProgress> createInitialLessons(List<LessonInfo> lessons) {
        if (lessons == null || lessons.isEmpty()) return new ArrayList<>();
        return lessons.stream()
                .map(lesson -> LessonProgress.builder()
                        .lessonId(lesson.getLessonId())
                        .lessonTitle(lesson.getLessonTitle())
                        .lessonOrder(lesson.getLessonOrder())
                        .status(LessonStatus.NOT_STARTED)
                        .attemptCount(0)
                        .timeSpentMinutes(0)
                        .watchPercentage(0.0)
                        .build())
                .toList();
    }

    private ProgressStats createInitialStats(List<LessonInfo> lessons) {
        int totalLessons = lessons != null ? lessons.size() : 0;
        return ProgressStats.builder()
                .totalLessons(totalLessons)
                .completedLessons(0)
                .inProgressLessons(0)
                .notStartedLessons(totalLessons)
                .completionPercentage(0.0)
                .totalTimeSpentMinutes(0)
                .averageTimePerLesson(0)
                .currentStreak(0)
                .longestStreak(0)
                .build();
    }
}
