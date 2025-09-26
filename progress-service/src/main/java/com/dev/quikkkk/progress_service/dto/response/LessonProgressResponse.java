package com.dev.quikkkk.progress_service.dto.response;

import com.dev.quikkkk.progress_service.enums.LessonStatus;
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
public class LessonProgressResponse {
    private String lessonId;
    private String lessonTitle;
    private Integer lessonOrder;
    private LessonStatus status;

    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private LocalDateTime lastAccessedAt;

    private Integer timeSpentMinutes;
    private Double watchPercentage;
    private String studentNotes;
    private Integer attemptCount;
}
