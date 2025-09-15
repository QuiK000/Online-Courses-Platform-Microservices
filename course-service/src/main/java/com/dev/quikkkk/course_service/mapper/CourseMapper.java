package com.dev.quikkkk.course_service.mapper;

import com.dev.quikkkk.course_service.dto.request.CreateCourseRequest;
import com.dev.quikkkk.course_service.dto.response.CourseResponse;
import com.dev.quikkkk.course_service.entity.Course;
import org.springframework.stereotype.Service;

@Service
public class CourseMapper {
    public CourseResponse toCourseResponse(Course course) {
        return CourseResponse
                .builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .teacherId(course.getTeacherId())
                .imageUrl(course.getImageUrl())
                .build();
    }

    public Course toCourse(CreateCourseRequest request, String teacherId) {
        return Course
                .builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .teacherId(teacherId)
                .imageUrl(request.getImageUrl())
                .build();
    }
}
