package com.dev.quikkkk.progress_service.document;

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
public class ProgressStats {
    private Integer totalLessons;
    private Integer completedLessons;
    private Integer inProgressLessons;
    private Integer notStartedLessons;

    private Double completionPercentage;
    private Integer totalTimeSpentMinutes;
    private Integer averageTimePerLesson;

    private LocalDateTime estimatedCompletionDate;
    private Integer estimatedRemainingHours;

    private Integer currentStreak;
    private Integer longestStreak;
    private LocalDateTime lastStudyDate;
}
