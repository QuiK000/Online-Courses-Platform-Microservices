package com.dev.quikkkk.progress_service.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
public class CompleteLessonRequest {
    @NotNull(message = "VALIDATION.COMPLETE.LESSON.COURSE.ID.NOT_BLANK")
    private String courseId;
    @Min(value = 0, message = "VALIDATION.COMPLETE.LESSON.TIME.SPENT.CANNOT.BE.NEGATIVE")
    private Integer timeSpentMinutes;
    @DecimalMin("0.0") @DecimalMax("100.0")
    private Double watchPercentage;
    private String notes;
    private LocalDateTime completedTime = LocalDateTime.now();
}
