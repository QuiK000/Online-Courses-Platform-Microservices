package com.dev.quikkkk.notification_service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProgressMilestoneEvent {
    private String studentId;
    private String studentEmail;
    private String studentName;
    private String courseId;
    private String courseName;
    private Double completionPercentage;
    private String milestone;
    private Integer completedLessons;
    private Integer totalLessons;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime achievedAt;
    private String templateType;
}
