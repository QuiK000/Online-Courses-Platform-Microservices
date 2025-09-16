package com.dev.quikkkk.course_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoursesAndLessonsResponse {
    private CourseResponse course;
    private List<LessonResponse> lessons;
}
