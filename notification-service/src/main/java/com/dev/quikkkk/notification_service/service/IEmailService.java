package com.dev.quikkkk.notification_service.service;

import jakarta.mail.MessagingException;

import java.util.concurrent.CompletableFuture;

public interface IEmailService {
    void sendCodeSuccessEmail(String destinationEmail, String code) throws MessagingException;

    CompletableFuture<Boolean> sendEmailAsync(String to, String subject, String content);
}
