package com.dev.quikkkk.progress_service.kafka;

import com.dev.quikkkk.progress_service.dto.kafka.StudentEnrolledEvent;
import com.dev.quikkkk.progress_service.dto.request.internal.EnrollStudentRequest;
import com.dev.quikkkk.progress_service.service.IProgressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentEnrollmentConsumer {
    private final IProgressService service;

    @KafkaListener(topics = "student-enrolled-topic", groupId = "progress-service")
    public void handleStudentEnrolled(StudentEnrolledEvent event) {
        log.info("Received StudentEnrolledEvent: student={}, course={}", event.getStudentId(), event.getCourseId());

        try {
            EnrollStudentRequest request = EnrollStudentRequest.builder()
                    .studentId(event.getStudentId())
                    .courseId(event.getCourseId())
                    .teacherId(event.getTeacherId())
                    .lessons(event.getLessons())
                    .build();

            service.createProgress(request);
            log.info("Successfully created progress for student {} in course {}", event.getStudentId(), event.getCourseId());
        } catch (Exception e) {
            log.error(
                    "Failed to create progress for student {} in course {}:  {}",
                    event.getStudentId(), event.getCourseId(), e.getMessage()
            );

            // TODO: retry or dead letter queue
        }
    }
}
