package com.dev.quikkkk.progress_service.repository;

import com.dev.quikkkk.progress_service.document.StudentActivity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IStudentActivityRepository extends MongoRepository<StudentActivity, String> {
    List<StudentActivity> findByStudentIdOrderByTimestampDesc(String studentId, Pageable pageable);

    List<StudentActivity> findByStudentIdAndCourseIdOrderByTimestampDesc(String studentId, String courseId, Pageable pageable);

    List<StudentActivity> findByCourseIdOrderByTimestampDesc(String courseId, Pageable pageable);

    List<StudentActivity> findByStudentIdAndTimestampBetween(String studentId, LocalDateTime start, LocalDateTime end);
}
