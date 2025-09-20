package com.dev.quikkkk.notification_service.service.impl;

import com.dev.quikkkk.notification_service.service.IEmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.HashMap;
import java.util.Map;

import static com.dev.quikkkk.notification_service.dto.EmailTemplate.CODE_CONFIRMATION;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.mail.javamail.MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements IEmailService {
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Override
    @Async
    public void sendCodeSuccessEmail(String destinationEmail, String code) throws MessagingException {
        var mimeMessage = mailSender.createMimeMessage();
        var helper = new MimeMessageHelper(mimeMessage, MULTIPART_MODE_MIXED_RELATED, UTF_8.name());

        helper.setFrom("contact@localhost");
        String templateName = CODE_CONFIRMATION.getTemplate();

        Map<String, Object> variables = new HashMap<>();
        variables.put("code", code);

        Context context = new Context();

        context.setVariables(variables);
        helper.setSubject(CODE_CONFIRMATION.getSubject());

        try {
            String html = templateEngine.process(templateName, context);

            helper.setText(html, true);
            helper.setTo(destinationEmail);

            mailSender.send(mimeMessage);
            log.info("Email sent to {}", destinationEmail);
        } catch (MessagingException e) {
            log.error("Error sending email to {}", destinationEmail, e);
        }
    }
}
