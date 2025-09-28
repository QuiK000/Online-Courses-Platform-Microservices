package com.dev.quikkkk.notification_service.dto;

import lombok.Getter;

public enum EmailTemplate {
    CODE_CONFIRMATION("code-confirmation.html", "Code to confirm email"),
    PROGRESS_MILESTONE("progress-milestone.html", "🎉 Milestone Achievement!"),
    LESSON_COMPLETED("lesson-completed.html", "✅ Great Job Completing Your Lesson!"),
    WEEKLY_PROGRESS_REPORT("weekly-progress-report.html", "📊 Your Weekly Learning Summary"),
    COURSE_COMPLETED("course-completed.html", "🏆 Congratulations! Course Completed!"),
    LESSON_REMINDER("lesson-reminder.html", "📚 Don't Forget Your Learning Goal!");

    @Getter
    private final String template;
    @Getter
    private final String subject;

    EmailTemplate(String template, String subject) {
        this.template = template;
        this.subject = subject;
    }
}
