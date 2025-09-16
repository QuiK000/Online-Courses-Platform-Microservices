package com.dev.quikkkk.course_service.service.impl;

import com.dev.quikkkk.course_service.dto.request.CreateCourseRequest;
import com.dev.quikkkk.course_service.dto.request.UpdateCourseRequest;
import com.dev.quikkkk.course_service.dto.response.CourseResponse;
import com.dev.quikkkk.course_service.dto.response.CoursesAndLessonsResponse;
import com.dev.quikkkk.course_service.dto.response.LessonResponse;
import com.dev.quikkkk.course_service.entity.Course;
import com.dev.quikkkk.course_service.entity.Lesson;
import com.dev.quikkkk.course_service.exception.BusinessException;
import com.dev.quikkkk.course_service.mapper.CourseMapper;
import com.dev.quikkkk.course_service.mapper.LessonMapper;
import com.dev.quikkkk.course_service.repository.ICourseRepository;
import com.dev.quikkkk.course_service.repository.ILessonRepository;
import com.dev.quikkkk.course_service.service.ICourseService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.dev.quikkkk.course_service.exception.ErrorCode.COURSE_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseServiceImpl implements ICourseService {
    private final ICourseRepository courseRepository;
    private final ILessonRepository lessonRepository;
    private final CourseMapper courseMapper;
    private final LessonMapper lessonMapper;

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "courses", allEntries = true),
            @CacheEvict(value = "courseWithLessons", key = "#result.id", condition = "#result != null")
    })
    public void createCourse(CreateCourseRequest request, String teacherId) {
        var course = courseMapper.toCourse(request, teacherId);
        courseRepository.save(course);
        log.info("Course created with ID: {}", course.getId());
    }

    @Override
    @Cacheable(value = "courses", unless = "#result == null or #result.isEmpty()")
    public List<CourseResponse> getAllCourses() {
        log.info("Fetching all courses");
        return courseRepository
                .findAll()
                .stream()
                .map(courseMapper::toCourseResponse)
                .toList();
    }

    @Override
    @Cacheable(value = "courseWithLessons", key = "#id", unless = "#result == null")
    public CoursesAndLessonsResponse getCourseWithLessons(String id) {
        log.info("Fetching course with ID: {}", id);
        Course course = courseRepository.findById(id).orElseThrow(() -> new BusinessException(COURSE_NOT_FOUND));
        List<Lesson> lessons = lessonRepository.findByCourseIdOrderByOrderAsc(id);

        CourseResponse courseResponse = courseMapper.toCourseResponse(course);
        List<LessonResponse> lessonResponses = lessons.stream()
                .map(lessonMapper::toLessonResponse)
                .toList();

        return CoursesAndLessonsResponse.builder()
                .course(courseResponse)
                .lessons(lessonResponses)
                .build();
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "courses", allEntries = true),
            @CacheEvict(value = "courseWithLessons", key = "#id")
    })
    public CourseResponse updateCourse(String id, UpdateCourseRequest request) {
        log.info("Updating course with ID: {}", id);
        return null;
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "courses", allEntries = true),
            @CacheEvict(value = "courseWithLessons", key = "#id")
    })
    public void deleteCourse(String id) {
        log.info("Deleting course with ID: {}", id);
        courseRepository.deleteById(id);
    }
}
