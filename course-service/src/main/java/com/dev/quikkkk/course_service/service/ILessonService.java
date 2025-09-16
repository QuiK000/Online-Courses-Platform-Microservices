package com.dev.quikkkk.course_service.service;

import com.dev.quikkkk.course_service.dto.request.CreateLessonRequest;
import com.dev.quikkkk.course_service.dto.response.LessonResponse;

import java.util.List;

public interface ILessonService {
    LessonResponse saveLesson(String courseId, CreateLessonRequest request);

    List<LessonResponse> getLessonsByCourseId(String courseId);

    LessonResponse getLessonById(String lessonId);

    void deleteLessonById(String lessonId);
}
