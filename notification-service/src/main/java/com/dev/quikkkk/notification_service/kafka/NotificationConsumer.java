package com.dev.quikkkk.notification_service.kafka;

import com.dev.quikkkk.notification_service.document.Notification;
import com.dev.quikkkk.notification_service.dto.CodeConfirmation;
import com.dev.quikkkk.notification_service.dto.LessonCompletedEvent;
import com.dev.quikkkk.notification_service.dto.ProgressMilestoneEvent;
import com.dev.quikkkk.notification_service.repository.INotificationRepository;
import com.dev.quikkkk.notification_service.service.IEmailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static com.dev.quikkkk.notification_service.document.NotificationType.LESSON_COMPLETED;
import static com.dev.quikkkk.notification_service.document.NotificationType.PROGRESS_MILESTONE;
import static com.dev.quikkkk.notification_service.document.NotificationType.SEND_EMAIL_VERIFICATION;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {
    private final INotificationRepository repository;
    private final IEmailService emailService;

    @KafkaListener(topics = "code-topic", groupId = "notification-service")
    public void consumeCodeEmailVerification(CodeConfirmation notification) throws MessagingException {
        log.info("Received email verification notification: {}", notification);
        try {
            repository.save(
                    Notification.builder()
                            .type(SEND_EMAIL_VERIFICATION)
                            .userId(notification.userId())
                            .code(notification.code())
                            .email(notification.email())
                            .notificationDate(LocalDateTime.now())
                            .build()
            );

            emailService.sendCodeSuccessEmail(
                    notification.email(),
                    notification.code()
            );

            log.info("Email verification notification sent successfully to: {}", notification.email());
        } catch (Exception e) {
            log.error("Failed to send email verification notification to: {}", notification.email(), e);
            throw e;
        }
    }

    @KafkaListener(topics = "progress-milestone-topic", groupId = "notification-service")
    public void handleProgressMilestone(ProgressMilestoneEvent event) throws MessagingException {
        log.info(
                "Received progress milestone notification for student: {}, milestone: {}",
                event.getStudentEmail(), event.getMilestone()
        );

        try {
            repository.save(
                    Notification.builder()
                            .type(PROGRESS_MILESTONE)
                            .userId(event.getStudentId())
                            .email(event.getMilestone())
                            .notificationDate(LocalDateTime.now())
                            .build()
            );

            Map<String, Object> variables = createMilestoneVariables(event);
            emailService.sendProgressMilestoneEmail(
                    event.getStudentEmail(),
                    event.getStudentName(),
                    variables
            );

            log.info("Progress milestone email sent successfully to: {}", event.getStudentEmail());
        } catch (Exception e) {
            log.error("Failed to send progress milestone email to: {}", event.getStudentEmail(), e);
            throw e;
        }
    }

    @KafkaListener(topics = "lesson-completed-topic", groupId = "notification-service")
    public void handleLessonCompleted(LessonCompletedEvent event) {
        log.info("Received lesson completed notification for student: {}, lesson: {}",
                event.getStudentEmail(), event.getLessonTitle());

        try {
            repository.save(
                    Notification.builder()
                            .type(LESSON_COMPLETED)
                            .userId(event.getStudentId())
                            .email(event.getStudentEmail())
                            .notificationDate(LocalDateTime.now())
                            .build()
            );

            Map<String, Object> variables = createLessonCompletedVariables(event);
            emailService.sendLessonCompletedEmail(
                    event.getStudentEmail(),
                    event.getStudentName(),
                    variables
            );

            log.info("Lesson completed email sent successfully to: {}", event.getStudentEmail());
        } catch (Exception e) {
            log.error("Failed to send lesson completed email to: {}", event.getStudentEmail(), e);
        }
    }

    private Map<String, Object> createMilestoneVariables(ProgressMilestoneEvent event) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("studentName", event.getStudentName());
        variables.put("courseName", event.getCourseName());
        variables.put("milestone", event.getMilestone());
        variables.put("completionPercentage", String.format("%.0f", event.getCompletionPercentage()));
        variables.put("completedLessons", event.getCompletedLessons());
        variables.put("totalLessons", event.getTotalLessons());
        variables.put("remainingLessons", event.getTotalLessons() - event.getCompletedLessons());
        variables.put("isCompleted", event.getCompletionPercentage() >= 100.0);
        variables.put("achievedAt", formatDateTime(event.getAchievedAt()));
        variables.put("progressBarWidth", String.format("%.0f", event.getCompletionPercentage()));

        if (event.getCompletionPercentage() >= 100.0) {
            variables.put("congratsMessage", "🎉 Congratulations! You've completed the entire course!");
            variables.put("nextStepMessage", "Ready for your next learning adventure?");
        } else if (event.getCompletionPercentage() >= 75.0) {
            variables.put("congratsMessage", "🚀 Amazing! You're almost at the finish line!");
            variables.put("nextStepMessage", "Just a few more lessons to go!");
        } else if (event.getCompletionPercentage() >= 50.0) {
            variables.put("congratsMessage", "💪 Great job! You're halfway there!");
            variables.put("nextStepMessage", "Keep up the excellent work!");
        } else {
            variables.put("congratsMessage", "🌟 Well done! You're making great progress!");
            variables.put("nextStepMessage", "Every step brings you closer to your goal!");
        }

        return variables;
    }

    private Map<String, Object> createLessonCompletedVariables(LessonCompletedEvent event) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("studentName", event.getStudentName());
        variables.put("courseName", event.getCourseName());
        variables.put("lessonTitle", event.getLessonTitle());
        variables.put("timeSpentMinutes", event.getTimeSpentMinutes());
        variables.put("timeSpentFormatted", formatTime(event.getTimeSpentMinutes()));
        variables.put("completedAt", formatDateTime(event.getCompletedAt()));
        variables.put("completedDate", formatDate(event.getCompletedAt()));
        variables.put("completedTime", formatTime(event.getCompletedAt()));

        if (event.getTimeSpentMinutes() != null && event.getTimeSpentMinutes() < 10) {
            variables.put("speedMessage", "⚡ Lightning fast! You're really getting the hang of this!");
        } else if (event.getTimeSpentMinutes() != null && event.getTimeSpentMinutes() > 30) {
            variables.put("speedMessage", "🧠 Thorough learning! Taking time to understand is key to mastery!");
        } else {
            variables.put("speedMessage", "👍 Perfect pace! You're learning efficiently!");
        }

        return variables;
    }

    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return "Unknown time";
        return dateTime.format(DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' HH:mm"));
    }

    private String formatDate(LocalDateTime dateTime) {
        if (dateTime == null) return "Unknown date";
        return dateTime.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"));
    }

    private String formatTime(LocalDateTime dateTime) {
        if (dateTime == null) return "Unknown time";
        return dateTime.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    private String formatTime(Integer minutes) {
        if (minutes == null || minutes == 0) return "0 minutes";
        if (minutes < 60) return minutes + " minute" + (minutes == 1 ? "" : "s");

        int hours = minutes / 60;
        int remainingMinutes = minutes % 60;

        String result = hours + " hour" + (hours == 1 ? "" : "s");
        if (remainingMinutes > 0) {
            result += " and " + remainingMinutes + " minute" + (remainingMinutes == 1 ? "" : "s");
        }
        return result;
    }
}
