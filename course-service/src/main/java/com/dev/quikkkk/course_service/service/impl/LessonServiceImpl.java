package com.dev.quikkkk.course_service.service.impl;

import com.dev.quikkkk.course_service.dto.request.CreateLessonRequest;
import com.dev.quikkkk.course_service.dto.response.LessonResponse;
import com.dev.quikkkk.course_service.exception.BusinessException;
import com.dev.quikkkk.course_service.mapper.LessonMapper;
import com.dev.quikkkk.course_service.repository.ICourseRepository;
import com.dev.quikkkk.course_service.repository.ILessonRepository;
import com.dev.quikkkk.course_service.service.ILessonService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.dev.quikkkk.course_service.exception.ErrorCode.COURSE_NOT_FOUND;
import static com.dev.quikkkk.course_service.exception.ErrorCode.LESSON_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class LessonServiceImpl implements ILessonService {
    private final ILessonRepository lessonRepository;
    private final ICourseRepository courseRepository;
    private final LessonMapper mapper;

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "courseWithLessons", key = "#courseId"),
            @CacheEvict(value = "lessonsByCourse", key = "#courseId")
    })
    public LessonResponse saveLesson(String courseId, CreateLessonRequest request) {
        Integer maxOrder = lessonRepository.findMaxOrderByCourseId(courseId);
        int newOrder = (maxOrder == null ? 1 : maxOrder + 1);

        var course = courseRepository.findById(courseId).orElseThrow(() -> new BusinessException(COURSE_NOT_FOUND));
        var lesson = mapper.toLesson(request, course, newOrder);
        var savedLesson = lessonRepository.save(lesson);

        log.info("Lesson created with ID: {} for course ID: {}", savedLesson.getId(), courseId);

        return mapper.toLessonResponse(savedLesson);
    }

    @Override
    @Cacheable(value = "lessonsByCourse", key = "#courseId", unless = "#result == null or #result.isEmpty()")
    public List<LessonResponse> getLessonsByCourseId(String courseId) {
        log.info("Fetching lessons for course ID: {}", courseId);
        if (!courseRepository.existsById(courseId)) throw new BusinessException(COURSE_NOT_FOUND);

        return lessonRepository.findByCourseIdOrderByOrderAsc(courseId).stream()
                .map(mapper::toLessonResponse)
                .toList();
    }

    @Override
    @Transactional
    @Cacheable(value = "singleLesson", key = "#lessonId", unless = "#result == null")
    public LessonResponse getLessonById(String lessonId) {
        log.info("Fetching lesson with ID: {}", lessonId);
        var lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new BusinessException(LESSON_NOT_FOUND));

        return mapper.toLessonResponse(lesson);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "courseWithLessons", key = "#courseId"),
            @CacheEvict(value = "lessonsByCourse", key = "#courseId")
    })
    public void deleteLesson(String lessonId, String courseId) {
        if (lessonRepository.existsById(lessonId)) {
            lessonRepository.deleteById(lessonId);
            log.info("Lesson deleted with ID: {} for course ID: {}", lessonId, courseId);
        }
    }
}
