package com.dev.quikkkk.course_service.service;

import com.dev.quikkkk.course_service.dto.request.CreateCourseRequest;
import com.dev.quikkkk.course_service.dto.request.UpdateCourseRequest;
import com.dev.quikkkk.course_service.dto.response.CourseResponse;

import java.util.List;

public interface ICourseService {
    void createCourse(CreateCourseRequest request, String teacherId);

    List<CourseResponse> getAllCourses();

    // TODO: Implement details course and lessons

    CourseResponse updateCourse(String id, UpdateCourseRequest request);

    void deleteCourse(String id);
}
