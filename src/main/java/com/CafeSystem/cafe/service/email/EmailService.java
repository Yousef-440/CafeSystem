package com.CafeSystem.cafe.service.email;

import com.CafeSystem.cafe.model.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
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


    public void sendResetLink(String to, String token) throws MessagingException, IOException {
        String resetLink = "http://localhost:8081/api/v1/user/restPassword?passwordRestToken=" + token;
        log.info("The token is: {}", token);
        ClassPathResource resource = new ClassPathResource("templates/rest.html");
        String content = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        content = content.replace("{{RESET_LINK}}", resetLink);

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject("Password-Reset");
        helper.setText(content, true);

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
