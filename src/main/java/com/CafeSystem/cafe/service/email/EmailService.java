package com.CafeSystem.cafe.service.email;

import com.CafeSystem.cafe.model.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import org.thymeleaf.context.Context;



@Service
public class EmailService {
    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${GMAIL_APP_EMAIL}")
    private String fromEmail;

    public void sendEmailToAdmins(String subject, String text, List<User> receivers) {
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


    public void sendResetLink(String to, String token, String name) throws MessagingException, IOException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject("Password-Reset");

        String resetLink = "http://localhost:8081/api/v1/user/resetPassword?passwordRestToken=" + token;

        String htmlContent = """
        <html>
        <head>
            <meta charset="UTF-8">
            <title>Password Reset</title>
        </head>
        <body style="font-family: Arial, sans-serif; background-color: #f5f5f5; padding: 20px; margin: 0;">
            <div style="max-width: 600px; margin: auto; background-color: #ffffff; padding: 30px; border-radius: 8px; box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);">
                <div style="font-size: 24px; color: #333; margin-bottom: 20px;">
                    Password Reset Request
                </div>
                <div style="font-size: 16px; color: #444; line-height: 1.6;">
                    <p>Dear, {name}</p>
                    <p>We received a request to reset your password for your <strong>Cafe System</strong> account.</p>
                    <p>Click the button below to reset your password:</p>
                    <a href="{resetLink}" style="display: inline-block; margin-top: 20px; padding: 12px 24px; background-color: #007bff; color: #ffffff; text-decoration: none; border-radius: 5px; font-weight: bold;">Reset Password</a>
       
                    <p style="margin-top: 20px;">This link will expire in 15 minutes for your security.</p>
                </div>
      
                <div style="margin-top: 40px; font-size: 12px; color: #999; border-top: 1px solid #eee; padding-top: 15px; text-align: center;">
                    If you didn't request this, please ignore this email.<br>
                    &copy; 2025 Cafe System. All rights reserved.
                </div>
            </div>
        </body>
        </html>
       """.replace("{name}", name).replace("{resetLink}", resetLink);

        helper.setText(htmlContent, true);
        mailSender.send(mimeMessage);
    }

    public void sendWhenSignup(String to, String subject, String name, String url) throws MessagingException{
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);

        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("VerificationUrl", url);


        String htmlContent = templateEngine.process("EmailSignup",context);
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }

    public void sendWhenChangePassword(String to, String name) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject("Password change notification");

        String htmlContent = """
        <html>
            <body style="font-family: Arial, sans-serif; line-height: 1.6;">
                <div style="max-width: 600px; margin: 0 auto; padding: 20px;">
                    <h2 style="color: #2c3e50;">Password Changed Successfully</h2>
                    <p>Hello, {name}</p>
                    <p>Your password was successfully updated on <span style="font-weight: bold;">{date}</span>.</p>
        
                    <div style="background-color: #f8f9fa; padding: 15px; border-left: 4px solid #dc3545; margin: 20px 0;">
                        <p style="margin: 0;">If you didn't make this change, please contact our support team immediately.</p>
                    </div>
        
                    <p>Stay secure,<br>The Cafe System Team</p>
                </div>
            </body>
        </html>
        """.replace("{date}", LocalDate.now().toString()).replace("{name}", name);

        helper.setText(htmlContent, true);
        mailSender.send(message);
    }

    public void sendBillToUser(String email, String subject, byte[] pdfBytes) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message, true);

        messageHelper.setFrom(fromEmail);
        messageHelper.setTo(email);
        messageHelper.setSubject(subject);

        messageHelper.setText("Thank you for your purchase! Your Bill is attached.", false);
        messageHelper.addAttachment("Bill.pdf",new ByteArrayResource(pdfBytes));

        mailSender.send(message);
    }

}
