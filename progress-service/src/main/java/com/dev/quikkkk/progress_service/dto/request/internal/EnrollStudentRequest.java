package com.dev.quikkkk.progress_service.dto.request.internal;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class EnrollStudentRequest {
    @NotNull(message = "VALIDATION.ENROLL.STUDENT.STUDENT.ID.NOT_NULL")
    private String studentId;
    @NotNull(message = "VALIDATION.ENROLL.STUDENT.STUDENT.NAME.NOT_NULL")
    private String courseId;
    @NotNull(message = "VALIDATION.ENROLL.STUDENT.COURSE.NAME.NOT_NULL")
    private String teacherId;
    private List<LessonInfo> lessons;
}
