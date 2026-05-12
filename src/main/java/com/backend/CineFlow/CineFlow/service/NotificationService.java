package com.backend.CineFlow.CineFlow.service;

import com.backend.CineFlow.CineFlow.dto.NotificationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final JavaMailSender mailSender;

    public void send(NotificationRequest request) {
        validarRequest(request);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(request.getTo());
        message.setSubject(request.getSubject());
        message.setText(request.getBody());

        if (mailSender instanceof JavaMailSenderImpl senderImpl && senderImpl.getUsername() != null) {
            message.setFrom(senderImpl.getUsername());
        }

        mailSender.send(message);
    }

    private void validarRequest(NotificationRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("El cuerpo de la solicitud es obligatorio");
        }
        if (request.getTo() == null || request.getTo().isBlank()) {
            throw new IllegalArgumentException("El destinatario es obligatorio");
        }
        if (request.getSubject() == null || request.getSubject().isBlank()) {
            throw new IllegalArgumentException("El asunto es obligatorio");
        }
        if (request.getBody() == null || request.getBody().isBlank()) {
            throw new IllegalArgumentException("El contenido es obligatorio");
        }
    }
}
