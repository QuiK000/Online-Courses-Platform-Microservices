package com.dev.quikkkk.course_service.mapper;

import com.dev.quikkkk.course_service.dto.request.CreateLessonRequest;
import com.dev.quikkkk.course_service.dto.response.LessonResponse;
import com.dev.quikkkk.course_service.entity.Course;
import com.dev.quikkkk.course_service.entity.Lesson;
import org.springframework.stereotype.Service;

@Service
public class LessonMapper {
    public Lesson toLesson(CreateLessonRequest request, Course course, int order) {
        return Lesson
                .builder()
                .title(request.getTitle())
                .content(request.getContent())
                .imageUrl(request.getImageUrl())
                .videoUrl(request.getVideoUrl())
                .course(course)
                .order(order)
                .build();
    }

    public LessonResponse toLessonResponse(Lesson lesson) {
        return LessonResponse
                .builder()
                .id(lesson.getId())
                .title(lesson.getTitle())
                .content(lesson.getContent())
                .imageUrl(lesson.getImageUrl())
                .videoUrl(lesson.getVideoUrl())
                .order(lesson.getOrder())
                .courseId(lesson.getCourse().getId())
                .build();
    }
}
