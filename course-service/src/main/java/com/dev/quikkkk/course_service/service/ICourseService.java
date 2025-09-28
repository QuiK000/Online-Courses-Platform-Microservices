package com.dev.quikkkk.course_service.service;

import com.dev.quikkkk.course_service.dto.request.CreateCourseRequest;
import com.dev.quikkkk.course_service.dto.request.UpdateCourseRequest;
import com.dev.quikkkk.course_service.dto.response.CourseResponse;
import com.dev.quikkkk.course_service.dto.response.CoursesAndLessonsResponse;

import java.util.List;

public interface ICourseService {
    void createCourse(CreateCourseRequest request, String teacherId);

    List<CourseResponse> getAllCourses();

    CoursesAndLessonsResponse getCourseWithLessons(String id);

    CourseResponse updateCourse(String id, UpdateCourseRequest request);

    void enrollStudent(String courseId, String studentId);

    void deleteCourse(String id);
}
