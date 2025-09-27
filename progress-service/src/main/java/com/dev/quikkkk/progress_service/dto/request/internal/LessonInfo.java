package com.dev.quikkkk.progress_service.dto.request.internal;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class LessonInfo {
    @NotNull(message = "VALIDATION.LESSON.INFO.LESSON.ID.NOT_NULL")
    private String lessonId;
    @NotNull(message = "VALIDATION.LESSON.INFO.LESSON.TITLE.NOT_NULL")
    private Integer lessonOrder;
}
