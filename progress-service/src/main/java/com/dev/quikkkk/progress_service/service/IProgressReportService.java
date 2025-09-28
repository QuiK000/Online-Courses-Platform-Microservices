package com.dev.quikkkk.progress_service.service;

public interface IProgressReportService {
    void generateAndSendWeeklyReports();

    void sendLessonReminders();

    void sendWeeklyReportForStudent(String studentId);
}
