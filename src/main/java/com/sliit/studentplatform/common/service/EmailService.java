package com.sliit.studentplatform.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendOtpEmail(String to, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("sliit.hub.noreply@gmail.com");
        message.setTo(to);
        message.setSubject("Your SLIIT Hub Login Code");
        message.setText("Your verification code is: " + otp + "\n\nExpires in 5 minutes.");
        mailSender.send(message);
    }
}