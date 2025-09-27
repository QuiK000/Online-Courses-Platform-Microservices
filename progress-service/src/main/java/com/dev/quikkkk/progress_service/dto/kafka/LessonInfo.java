package com.dev.quikkkk.progress_service.dto.kafka;

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
    private String lessonId;
    private String lessonTitle;
    private Integer lessonOrder;
}
