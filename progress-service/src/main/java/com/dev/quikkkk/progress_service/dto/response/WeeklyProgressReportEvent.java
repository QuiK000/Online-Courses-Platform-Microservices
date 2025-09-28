package com.dev.quikkkk.progress_service.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record WeeklyProgressReportEvent(
        String studentId,
        String studentEmail,
        String studentName,
        List<CourseProgressSummary> weeklyProgress,
        LocalDateTime weekStart,
        LocalDateTime weekEnd,
        Integer totalLessonsCompleted,
        Integer totalTimeSpent,
        String templateType
) {
    @Builder
    public record CourseProgressSummary(
            String courseId,
            String courseName,
            Integer lessonsCompletedThisWeek,
            Integer timeSpentThisWeek,
            Double progressIncrease
    ) {}
}
