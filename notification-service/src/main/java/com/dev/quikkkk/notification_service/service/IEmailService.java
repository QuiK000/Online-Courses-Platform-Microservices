package com.dev.quikkkk.notification_service.service;

import jakarta.mail.MessagingException;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface IEmailService {
    void sendCodeSuccessEmail(String destinationEmail, String code) throws MessagingException;

    void sendProgressMilestoneEmail(String destinationEmail, String studentName, Map<String, Object> variables) throws MessagingException;

    void sendLessonCompletedEmail(String destinationEmail, String studentName, Map<String, Object> variables) throws MessagingException;

    void sendWeeklyProgressReport(String destinationEmail, String studentName, Map<String, Object> variables) throws MessagingException;

    CompletableFuture<Boolean> sendEmailAsync(String to, String subject, String content);
}
