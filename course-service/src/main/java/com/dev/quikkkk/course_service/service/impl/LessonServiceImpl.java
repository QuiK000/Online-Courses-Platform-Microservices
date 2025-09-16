package com.dev.quikkkk.course_service.service.impl;

import com.dev.quikkkk.course_service.dto.request.CreateLessonRequest;
import com.dev.quikkkk.course_service.dto.response.LessonResponse;
import com.dev.quikkkk.course_service.exception.BusinessException;
import com.dev.quikkkk.course_service.mapper.LessonMapper;
import com.dev.quikkkk.course_service.repository.ICourseRepository;
import com.dev.quikkkk.course_service.repository.ILessonRepository;
import com.dev.quikkkk.course_service.service.ILessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.dev.quikkkk.course_service.exception.ErrorCode.COURSE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class LessonServiceImpl implements ILessonService {
    private final ILessonRepository lessonRepository;
    private final ICourseRepository courseRepository;
    private final LessonMapper mapper;

    @Override
    public LessonResponse saveLesson(String courseId, CreateLessonRequest request) {
        Integer maxOrder = lessonRepository.findMaxOrderByCourseId(courseId);
        int newOrder = (maxOrder == null ? 1 : maxOrder + 1);

        var course = courseRepository.findById(courseId).orElseThrow(() -> new BusinessException(COURSE_NOT_FOUND));
        var lesson = mapper.toLesson(request, course, newOrder);
        var savedLesson = lessonRepository.save(lesson);

        return mapper.toLessonResponse(savedLesson);
    }
}
