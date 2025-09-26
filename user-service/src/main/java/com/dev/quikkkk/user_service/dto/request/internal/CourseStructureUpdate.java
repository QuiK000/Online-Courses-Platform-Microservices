package com.dev.quikkkk.user_service.dto.request.internal;

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
public class CourseStructureUpdate {
    private List<LessonInfo> addedLessons;
    private List<String> removedLessonIds;
    private List<LessonInfo> updatedLessons;
}
