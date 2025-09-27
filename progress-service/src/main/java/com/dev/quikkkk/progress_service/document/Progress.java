package com.dev.quikkkk.progress_service.document;

import com.dev.quikkkk.progress_service.enums.CourseStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "progress")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Progress {
    @Id
    private String id;
    private String studentId;
    private String courseId;
    private String teacherId;
    private List<LessonProgress> lessons = new ArrayList<>();
    private ProgressStats stats;
    private CourseStatus courseStatus;

    @CreatedDate
    private LocalDateTime enrolledAt;

    @LastModifiedDate
    private LocalDateTime lastActivityAt;
    private LocalDateTime completedAt;

    private Integer version;
}
