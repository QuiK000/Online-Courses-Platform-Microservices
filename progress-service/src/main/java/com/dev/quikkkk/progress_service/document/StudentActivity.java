package com.dev.quikkkk.progress_service.document;

import com.dev.quikkkk.progress_service.enums.ActivityType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

@Document(collection = "student_activities")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class StudentActivity {
    @Id
    private String id;
    private String studentId;
    private String courseId;
    private String lessonId;
    private ActivityType activityType;
    private Map<String, Object> metadata;
    private LocalDateTime timestamp;
}
