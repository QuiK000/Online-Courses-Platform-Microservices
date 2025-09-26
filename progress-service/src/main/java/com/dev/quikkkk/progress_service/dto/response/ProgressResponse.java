package com.dev.quikkkk.progress_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ProgressResponse {
    private String id;
    private String studentId;
    private String courseId;
    private LocalDateTime enrolledAt;
}
