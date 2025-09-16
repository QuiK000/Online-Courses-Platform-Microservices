package com.dev.quikkkk.course_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonResponse {
    private String id;
    private String title;
    private String content;
    private String imageUrl;
    private String videoUrl;
    private String courseId;
    private Integer order;
}
