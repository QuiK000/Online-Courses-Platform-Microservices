package com.dev.quikkkk.progress_service.dto.response;

import com.dev.quikkkk.progress_service.enums.CourseStatus;
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
public class StudentProgressSummary {
    private String courseId;
    private String courseName;
    private String courseImageUrl;
    private CourseStatus courseStatus;
    private Double completionPercentage;
    private Integer totalLessons;
    private Integer completedLessons;
    private LocalDateTime lastActivityAt;
}
