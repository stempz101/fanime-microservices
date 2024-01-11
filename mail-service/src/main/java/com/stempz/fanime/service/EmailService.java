package com.stempz.fanime.service;

import com.stempz.fanime.dto.EmailVerificationDto;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

  private final JavaMailSender javaMailSender;
  private final TemplateEngine templateEngine;

  @Value("${spring.mail.username}")
  private String senderEmail;

  @Value("${spring.mail.sender.name}")
  private String senderName;

  @Value("${app.link.email-verification}")
  private String emailVerificationLink;

  public void sendEmailVerificationLink(EmailVerificationDto emailVerificationDto) {
    try {
      MimeMessage message = javaMailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message);
      helper.setFrom(new InternetAddress(senderEmail, senderName));
      helper.setTo(emailVerificationDto.email());
      helper.setSubject("Fanime: New Account Verification");

      Context context = new Context();
      context.setVariable("verificationLink",
          String.format(
              "%s?token=%s",
              emailVerificationLink,
              emailVerificationDto.verificationToken().toString()
          )
      );
      String htmlBody = templateEngine.process("email-verification-template", context);

      helper.setText(htmlBody, true);

      log.info("Sending an email verification link to the specified email: {}",
          emailVerificationDto.email());
      javaMailSender.send(message);
      log.info("Email verification link successfully sent to {}", emailVerificationDto.email());
    } catch (MessagingException | UnsupportedEncodingException e) {
      log.error("Failed to send registration confirmation link to {}",
          emailVerificationDto.email());
      throw new RuntimeException(e);
    }
  }
}
