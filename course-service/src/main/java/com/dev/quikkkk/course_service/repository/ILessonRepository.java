package com.dev.quikkkk.course_service.repository;

import com.dev.quikkkk.course_service.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ILessonRepository extends JpaRepository<Lesson, String> {
    @Query("SELECT COALESCE(MAX(l.order), 0) FROM Lesson l WHERE l.course.id = :courseId")
    Integer findMaxOrderByCourseId(String courseId);
}
