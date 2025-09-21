package com.dev.quikkkk.notification_service.service.impl;

import com.dev.quikkkk.notification_service.service.IEmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static com.dev.quikkkk.notification_service.dto.EmailTemplate.CODE_CONFIRMATION;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.mail.javamail.MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements IEmailService {
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${spring.mail.from:noreply@quikkkk.dev}")
    private String fromEmail;

    @Override
    @Async
    @Retryable(backoff = @Backoff(delay = 2000, multiplier = 2))
    public void sendCodeSuccessEmail(String destinationEmail, String code) throws MessagingException {
        log.info("Sending code confirmation email to {}", destinationEmail);

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    mimeMessage,
                    MULTIPART_MODE_MIXED_RELATED,
                    UTF_8.name()
            );

            helper.setFrom(fromEmail);
            helper.setTo(destinationEmail);
            helper.setSubject(CODE_CONFIRMATION.getSubject());

            Map<String, Object> variables = new HashMap<>();

            variables.put("code", code);
            variables.put("expiryMinutes", 15);
            variables.put("supportEmail", fromEmail);

            Context context = new Context();
            context.setVariables(variables);

            String htmlContent = templateEngine.process(
                    CODE_CONFIRMATION.getTemplate(),
                    context
            );

            helper.setText(htmlContent, true);
            mailSender.send(mimeMessage);

            log.info("Verification email sent successfully to: {}", destinationEmail);
        } catch (MessagingException e) {
            log.error("Failed to send verification email to: {}", destinationEmail, e);
            throw e;
        }
    }

    @Override
    @Async
    public CompletableFuture<Boolean> sendEmailAsync(String to, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, UTF_8.name());

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);

            mailSender.send(message);
            return CompletableFuture.completedFuture(true);
        } catch (Exception e) {
            log.error("Failed to send email to: {}", to, e);
            return CompletableFuture.completedFuture(false);
        }
    }
}
