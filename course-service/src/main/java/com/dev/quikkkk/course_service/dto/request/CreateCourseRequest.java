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
@AllArgsConstructor
@NoArgsConstructor
public class CreateCourseRequest {
    @NotBlank(message = "VALIDATION.COURSE.TITLE.NOT_BLANK")
    @Size(min = 3, max = 120, message = "VALIDATION.COURSE.TITLE.SIZE")
    private String title;

    @NotBlank(message = "VALIDATION.COURSE.DESCRIPTION.NOT_BLANK")
    @Size(min = 3, max = 2500, message = "VALIDATION.COURSE.DESCRIPTION.SIZE")
    private String description;

    @NotBlank(message = "VALIDATION.COURSE.IMAGE_URL.NOT_BLANK")
    @URL(message = "VALIDATION.COURSE.IMAGE_URL.FORMAT")
    private String imageUrl;
}
