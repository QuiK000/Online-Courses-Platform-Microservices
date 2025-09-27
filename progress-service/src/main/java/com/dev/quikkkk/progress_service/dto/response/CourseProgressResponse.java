package com.dev.quikkkk.progress_service.dto.response;

import com.dev.quikkkk.progress_service.document.ProgressStats;
import com.dev.quikkkk.progress_service.enums.CourseStatus;
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
public class CourseProgressResponse {
    private String courseId;
    private String teacherId;

    private CourseStatus courseStatus;
    private ProgressStats stats;
    private List<LessonProgressResponse> lessons;

    private LocalDateTime enrolledAt;
    private LocalDateTime lastActivityAt;
    private LocalDateTime completedAt;
}
