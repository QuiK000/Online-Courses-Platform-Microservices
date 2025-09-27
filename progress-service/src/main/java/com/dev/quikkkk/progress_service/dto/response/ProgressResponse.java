package com.dev.quikkkk.progress_service.dto.response;

import com.dev.quikkkk.progress_service.dto.request.internal.LessonInfo;
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
public class ProgressResponse {
    private String id;
    private String studentId;
    private String courseId;
    private String teacherId;
    private List<LessonInfo> lessons;
    private LocalDateTime enrolledAt;
}
