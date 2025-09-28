package com.dev.quikkkk.progress_service.repository;

import com.dev.quikkkk.progress_service.document.Progress;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface IProgressRepository extends MongoRepository<Progress, String> {
    Optional<Progress> findByStudentIdAndCourseId(String studentId, String courseId);

    List<Progress> findByStudentId(String studentId);

    List<Progress> findByTeacherId(String teacherId);

    List<Progress> findByCourseId(String courseId);

    Page<Progress> findByTeacherId(String teacherId, Pageable pageable);

    Page<Progress> findByCourseId(String courseId, Pageable pageable);

    void deleteByLastActivityAtBefore(LocalDateTime cutoffDate);

    List<Progress> findByLastActivityAtBefore(LocalDateTime cutoffDate);

    List<Progress> findByLastActivityAtAfter(LocalDateTime dateTime);

    @Query("{ 'lastActivityAt' : { $lt: ?0 }, 'courseStatus' : { $ne: 'COMPLETED' } }")
    List<Progress> findInactiveStudents(LocalDateTime dateTime);
}
