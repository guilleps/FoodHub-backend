package com.project.FoodHub.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final String mailOrigin;

    public EmailService(
            JavaMailSender mailSender,
            @Value("${spring.mail.username}") String mailOrigin
    ) {
        this.mailSender = mailSender;
        this.mailOrigin = mailOrigin;
    }

    @Async("taskExecutor")
    public void sendEmail(String to, String subject, String content) {

        try {

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(mailOrigin);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);
            mailSender.send(message);

            log.info("Correo enviado correctamente a {}", to);

        } catch (Exception e) {
            log.error("Error al enviar correo a {}: {}", to, e.getMessage());
            throw e;
        }
    }
}
