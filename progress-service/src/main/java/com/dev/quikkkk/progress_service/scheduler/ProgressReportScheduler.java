package com.dev.quikkkk.progress_service.scheduler;

import com.dev.quikkkk.progress_service.service.IProgressReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "app.scheduler.enabled", havingValue = "true", matchIfMissing = true)
public class ProgressReportScheduler {
    private final IProgressReportService service;

    @Scheduled(cron = "0 0 9 * * MON")
    public void sendWeeklyProgressReports() {
        log.info("Starting weekly progress report generation");
        try {
            service.generateAndSendWeeklyReports();
            log.info("Weekly progress report sent successfully");
        } catch (Exception e) {
            log.error("Weekly progress report generation failed");
        }
    }

    @Scheduled(cron = "0 0 18 * * *")
    public void sendLessonReminders() {
        log.info("Starting lesson reminder generation");
        try {
            service.sendLessonReminders();
            log.info("Daily lesson reminders sent successfully");
        } catch (Exception e) {
            log.error("Daily lesson reminders sent failed");
        }
    }
}
