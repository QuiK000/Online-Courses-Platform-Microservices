package com.dev.quikkkk.progress_service.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
    @NotNull(message = "VALIDATION.COMPLETE.LESSON.TIME.SPENT.NOT_NULL")
    @Positive(message = "VALIDATION.COMPLETE.LESSON.TIME.SPENT.POSITIVE")
    private Integer timeSpentMinutes;

    @DecimalMin("0.0") @DecimalMax("100.0")
    @NotNull(message = "VALIDATION.COMPLETE.LESSON.WATCH.PERCENTAGE.NOT_NULL")
    @Positive(message = "VALIDATION.COMPLETE.LESSON.WATCH.PERCENTAGE.POSITIVE")
    private Double watchPercentage;

    private String notes;
    private LocalDateTime completedTime = LocalDateTime.now();
}
