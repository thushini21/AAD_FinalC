package com.example.salooniveryvells.Service.Impl;

import com.example.salooniveryvells.Dto.EmailDTO;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("thushiniakashi58@gmail.com")
    private String fromEmail;

    public void sendEmail(EmailDTO emailDto) throws MessagingException {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);


            message.addHeader("X-Mailer", "HomeEase");
            message.addHeader("X-Priority", "1");

            helper.setFrom(fromEmail);
            helper.setTo(emailDto.getTo());
            helper.setSubject(emailDto.getSubject());
            helper.setText(emailDto.getContent(), true);

            System.out.println("Attempting to send email to: " + emailDto.getTo());
            System.out.println("Using SMTP host: " + ((JavaMailSenderImpl)mailSender).getHost());

            mailSender.send(message);
            System.out.println("Email sent successfully at " + LocalDateTime.now());

        } catch (Exception e) {
            System.err.println("Email sending failed:");
            e.printStackTrace();
            throw e;
        }
    }
}