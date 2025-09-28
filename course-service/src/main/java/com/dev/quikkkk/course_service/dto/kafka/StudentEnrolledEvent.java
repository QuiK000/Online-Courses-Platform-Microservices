package com.dev.quikkkk.course_service.dto.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class StudentEnrolledEvent {
    private String studentId;
    private String courseId;
    private String teacherId;
    private String courseName;
    private List<LessonInfo> lessons;
    private LocalDateTime enrolledAt;
}
