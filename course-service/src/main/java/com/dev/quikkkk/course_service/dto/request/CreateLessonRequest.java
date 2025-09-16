package com.dev.quikkkk.course_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateLessonRequest {
    @NotBlank(message = "VALIDATION.LESSON.TITLE.NOT_BLANK")
    @Size(min = 3, max = 120, message = "VALIDATION.LESSON.TITLE.SIZE")
    private String title;

    @NotBlank(message = "VALIDATION.LESSON.CONTENT.NOT_BLANK")
    @Size(min = 3, max = 2500, message = "VALIDATION.LESSON.CONTENT.SIZE")
    private String content;

    @NotBlank(message = "VALIDATION.LESSON.IMAGE_URL.NOT_BLANK")
    @URL(message = "VALIDATION.LESSON.IMAGE_URL.FORMAT")
    private String imageUrl;

    @NotBlank(message = "VALIDATION.LESSON.VIDEO_URL.NOT_BLANK")
    @URL(message = "VALIDATION.LESSON.VIDEO_URL.FORMAT")
    private String videoUrl;
}
