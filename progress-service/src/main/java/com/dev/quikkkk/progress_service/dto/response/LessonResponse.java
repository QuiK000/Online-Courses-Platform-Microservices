package com.dev.quikkkk.progress_service.dto.response;

import com.dev.quikkkk.progress_service.enums.LessonStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LessonResponse {
    private String lessonId;
    private LessonStatus status;
    private LocalDateTime completedAt;
}
