package com.jobportal.services;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
        } catch (Exception e) {
            // In a real application, you'd log this and potentially retry
            System.err.println("Failed to send email: " + e.getMessage());
        }
    }

    public void sendOtp(String email, String otp) {
        sendEmail(email, "Your OTP for Job Portal", "Your OTP is: " + otp + ". It will expire in 10 minutes.");
    }
}
