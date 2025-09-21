package com.dev.quikkkk.notification_service.kafka;

import com.dev.quikkkk.notification_service.document.Notification;
import com.dev.quikkkk.notification_service.dto.CodeConfirmation;
import com.dev.quikkkk.notification_service.repository.INotificationRepository;
import com.dev.quikkkk.notification_service.service.IEmailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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
                            .email(notification.email())
                            .code(notification.code())
                            .userId(notification.userId())
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
}
