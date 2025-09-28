package com.dev.quikkkk.notification_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class LessonCompletedEvent {
    private String studentId;
    private String studentEmail;
    private String studentName;
    private String courseId;
    private String courseName;
    private String lessonId;
    private String lessonTitle;
    private Integer timeSpentMinutes;
    private LocalDateTime completedAt;
    private String templateType;
}
