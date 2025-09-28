package com.dev.quikkkk.progress_service.service.impl;

import com.dev.quikkkk.progress_service.client.IUserServiceClient;
import com.dev.quikkkk.progress_service.document.Progress;
import com.dev.quikkkk.progress_service.dto.response.WeeklyProgressReportEvent;
import com.dev.quikkkk.progress_service.repository.IProgressRepository;
import com.dev.quikkkk.progress_service.service.IProgressReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProgressReportServiceImpl implements IProgressReportService {
    private final IProgressRepository repository;
    private final IUserServiceClient client;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void generateAndSendWeeklyReports() {
        log.info("Generating weekly progress reports for all active students");
        LocalDateTime weekStart = LocalDateTime.now().minusDays(7);
        List<Progress> activeProgress = repository.findByLastActivityAtBefore(weekStart);

        log.info("Found {} students with activity in the last week", activeProgress.size());
        activeProgress.forEach(progress -> {
            try {
                sendWeeklyReportForStudent(progress.getStudentId());
            } catch (Exception e) {
                log.error("Failed to send weekly report for student: {}", progress.getStudentId(), e);
            }
        });
    }

    @Override
    public void sendLessonReminders() {
        // TODO
    }

    @Override
    public void sendWeeklyReportForStudent(String studentId) {
        log.info("Generating weekly report for student: {}", studentId);

        try {
            var userInfo = client.getUserById(studentId).data();
            List<Progress> studentProgress = repository.findByStudentId(studentId);

            LocalDateTime weekStart = LocalDateTime.now().minusDays(7);
            LocalDateTime weekEnd = LocalDateTime.now();

            List<WeeklyProgressReportEvent.CourseProgressSummary> coursesSummary = studentProgress.stream()
                    .filter(p -> p.getLastActivityAt().isAfter(weekStart))
                    .map(this::createCourseSummary)
                    .toList();

            if (!coursesSummary.isEmpty()) {
                int totalLessonsCompleted = coursesSummary.stream()
                        .mapToInt(WeeklyProgressReportEvent.CourseProgressSummary::lessonsCompletedThisWeek)
                        .sum();
                int totalTimeSpent = coursesSummary.stream()
                        .mapToInt(WeeklyProgressReportEvent.CourseProgressSummary::timeSpentThisWeek)
                        .sum();

                WeeklyProgressReportEvent event = WeeklyProgressReportEvent.builder()
                        .studentId(studentId)
                        .studentEmail(userInfo.getEmail())
                        .studentName(userInfo.getFirstName() + " " + userInfo.getLastName())
                        .weeklyProgress(coursesSummary)
                        .weekStart(weekStart)
                        .weekEnd(weekEnd)
                        .totalLessonsCompleted(totalLessonsCompleted)
                        .totalTimeSpent(totalTimeSpent)
                        .templateType("WEEKLY_PROGRESS_REPORT")
                        .build();

                kafkaTemplate.send("weekly-progress-report-topic", event);
                log.info("Successfully send weekly report for student: {}", studentId);
            } else {
                log.info("No activity found for student {} in the last week", studentId);
            }
        } catch (Exception e) {
            log.error("Failed to send weekly report for student: {}", studentId, e);
        }
    }

    private WeeklyProgressReportEvent.CourseProgressSummary createCourseSummary(Progress progress) {
        // Calculate weekly progress for this course
        LocalDateTime weekStart = LocalDateTime.now().minusDays(7);

        int lessonsThisWeek = (int) progress.getLessons().stream()
                .filter(lesson -> lesson.getCompletedAt() != null && lesson.getCompletedAt().isAfter(weekStart))
                .count();

        int timeThisWeek = progress.getLessons().stream()
                .filter(lesson -> lesson.getLastAccessedAt() != null && lesson.getLastAccessedAt().isAfter(weekStart))
                .mapToInt(lesson -> lesson.getTimeSpentMinutes() != null ? lesson.getTimeSpentMinutes() : 0)
                .sum();

        return WeeklyProgressReportEvent.CourseProgressSummary.builder()
                .courseId(progress.getCourseId())
                .courseName("Learning Course") // TODO: Get actual course name
                .lessonsCompletedThisWeek(lessonsThisWeek)
                .timeSpentThisWeek(timeThisWeek)
                .progressIncrease(0.0) // TODO: Calculate actual progress increase
                .build();
    }
}
