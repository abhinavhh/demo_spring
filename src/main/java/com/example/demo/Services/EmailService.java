package com.example.demo.Services;


import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    
    private final JavaMailSender mailSender;
    private static final String FROM_EMAIL = "your-email@your-domain.com";

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOTP(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(FROM_EMAIL);
        message.setTo(toEmail);
        message.setSubject("Password Reset OTP");
        message.setText("Your OTP for password reset is: " + otp + "\nThis OTP will expire in 5 minutes.");
        
        try {
            mailSender.send(message);
        } catch (Exception e) {
            // Log the error and handle it appropriately
            throw new RuntimeException("Failed to send OTP email", e);
        }
    }
}