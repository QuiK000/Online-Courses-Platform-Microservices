package com.dev.quikkkk.progress_service.dto.request;

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
public class StartLessonRequest {
    @NotNull(message = "VALIDATION.START.LESSON.COURSE.ID.NOT_BLANK")
    private String courseId;
    private LocalDateTime startTime = LocalDateTime.now();
}
