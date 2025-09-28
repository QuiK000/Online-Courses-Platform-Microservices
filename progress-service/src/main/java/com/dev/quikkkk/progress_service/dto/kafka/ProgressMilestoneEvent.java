package com.dev.quikkkk.progress_service.dto.kafka;

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
public class ProgressMilestoneEvent {
    private String studentId;
    private String studentEmail;
    private String studentName;
    private String courseId;
    private String courseName;
    private Double completionPercentage;
    private String milestone;
    private Integer completedLessons;
    private LocalDateTime achievedAt;
    private String templateType = "PROGRESS_MILESTONE";
}
