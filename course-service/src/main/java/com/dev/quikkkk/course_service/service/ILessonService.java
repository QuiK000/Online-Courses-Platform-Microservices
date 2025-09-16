package com.dev.quikkkk.course_service.service;

import com.dev.quikkkk.course_service.dto.request.CreateLessonRequest;
import com.dev.quikkkk.course_service.dto.response.LessonResponse;

public interface ILessonService {
    LessonResponse saveLesson(String courseId, CreateLessonRequest request);
}
