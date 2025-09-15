package com.dev.quikkkk.course_service.service.impl;

import com.dev.quikkkk.course_service.dto.request.CreateCourseRequest;
import com.dev.quikkkk.course_service.dto.request.UpdateCourseRequest;
import com.dev.quikkkk.course_service.dto.response.CourseResponse;
import com.dev.quikkkk.course_service.mapper.CourseMapper;
import com.dev.quikkkk.course_service.repository.ICourseRepository;
import com.dev.quikkkk.course_service.service.ICourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseServiceImpl implements ICourseService {
    private final ICourseRepository repository;
    private final CourseMapper mapper;

    @Override
    public void createCourse(CreateCourseRequest request, String teacherId) {
        var course = mapper.toCourse(request, teacherId);
        repository.save(course);
    }

    @Override
    public List<CourseResponse> getAllCourses() {
        return repository
                .findAll()
                .stream()
                .map(mapper::toCourseResponse)
                .toList();
    }

    @Override
    public CourseResponse updateCourse(String id, UpdateCourseRequest request) {
        return null;
    }

    @Override
    public void deleteCourse(String id) {
        repository.deleteById(id);
    }
}
