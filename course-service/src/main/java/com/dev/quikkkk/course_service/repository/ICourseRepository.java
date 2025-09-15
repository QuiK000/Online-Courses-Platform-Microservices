package com.dev.quikkkk.course_service.repository;

import com.dev.quikkkk.course_service.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICourseRepository extends JpaRepository<Course, String> {

}
