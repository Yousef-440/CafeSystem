package com.CafeSystem.cafe.service.email;

import com.CafeSystem.cafe.model.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.thymeleaf.context.Context;



@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${GMAIL_APP_EMAIL}")
    private String fromEmail;

    public void sendAccountCreationEmail(String subject, String text, List<User> receivers) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(fromEmail);
        mailMessage.setTo(getToArray(receivers));
        mailMessage.setSubject(subject);
        mailMessage.setText(text);
        mailSender.send(mailMessage);
    }

    private String[] getToArray(List<User> users) {
        return users.stream()
                .map(User::getEmail)
                .toArray(String[]::new);
    }


    public void sendResetLink(String to, String token) throws MessagingException {
        String resetLink = "http://localhost:8080/reset-password?token=" + token;

        String htmlMessage = "<html>" +
                "<body style='font-family: Arial, sans-serif; background-color: #f5f5f5; padding: 20px;'>" +
                "<div style='max-width: 600px; margin: auto; background-color: #ffffff; padding: 30px; border-radius: 8px; box-shadow: 0 0 10px rgba(0,0,0,0.1);'>" +
                "<h2 style='color: #333333;'>🔐 Password Reset Request</h2>" +
                "<p>Hello,</p>" +
                "<p>We received a request to reset your password for your <strong>Cafe System</strong> account.</p>" +
                "<p>Click the button below to reset your password:</p>" +
                "<a href='" + resetLink + "' style='display: inline-block; padding: 12px 24px; background-color: #007bff; color: #fff; text-decoration: none; border-radius: 5px;'>Reset Password</a>" +
                "<p style='margin-top: 20px;'>This link will expire in 15 minutes.</p>" +
                "<hr style='margin-top: 30px;'/>" +
                "<p style='font-size: 12px; color: #999;'>If you didn’t request this, please ignore this email.</p>" +
                "<p style='font-size: 12px; color: #999;'>© 2025 Cafe System. All rights reserved.</p>" +
                "</div>" +
                "</body>" +
                "</html>";

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

        helper.setFrom("cafesystem00@gmail.com");
        helper.setTo(to);
        helper.setSubject("Password Reset Request");
        helper.setText(htmlMessage, true);

        mailSender.send(mimeMessage);
    }

    public void sendWhenSignup(String to, String subject, String name) throws MessagingException{
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);

        Context context = new Context();
        context.setVariable("name", name);


        String htmlContent = templateEngine.process("EmailSignup",context);
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }

    public void sendWhenChangePassword(String to){
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(fromEmail);
        mailMessage.setTo(to);
        mailMessage.setSubject("Password change successfully");
        mailMessage.setText("Hello,\n" +
                "\n" +
                "Your password was successfully updated. If you did not make this change, please contact our support team immediately.\n" +
                "\n" +
                "Stay secure,\n" +
                "Cafe System Team" +
                "\n");

        mailSender.send(mailMessage);

    }

}
