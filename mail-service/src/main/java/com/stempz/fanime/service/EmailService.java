package com.stempz.fanime.service;

import com.stempz.fanime.dto.EmailWithTokenDto;
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

  @Value("${app.link.reset-password}")
  private String resetPasswordLink;

  public void sendEmailVerificationLink(EmailWithTokenDto emailWithTokenDto) {
    try {
      MimeMessage message = javaMailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message);
      helper.setFrom(new InternetAddress(senderEmail, senderName));
      helper.setTo(emailWithTokenDto.email());
      helper.setSubject("Fanime: New Account Verification");

      Context context = new Context();
      context.setVariable("verificationLink",
          String.format(
              "%s?token=%s",
              emailVerificationLink,
              emailWithTokenDto.token().toString()
          )
      );
      String htmlBody = templateEngine.process("email-verification-template", context);

      helper.setText(htmlBody, true);

      log.info("Sending an email verification link to the specified email: {}",
          emailWithTokenDto.email());
      javaMailSender.send(message);
      log.info("Email verification link successfully sent to {}", emailWithTokenDto.email());
    } catch (MessagingException | UnsupportedEncodingException e) {
      log.error("Failed to send registration confirmation link to {}", emailWithTokenDto.email());
      throw new RuntimeException(e);
    }
  }

  public void sendResetInstructions(EmailWithTokenDto emailWithTokenDto) {
    try {
      MimeMessage message = javaMailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message);
      helper.setFrom(new InternetAddress(senderEmail, senderName));
      helper.setTo(emailWithTokenDto.email());
      helper.setSubject("Fanime: Password reset instructions");

      Context context = new Context();
      context.setVariable("resetPasswordLink",
          String.format(
              "%s?token=%s",
              resetPasswordLink,
              emailWithTokenDto.token().toString()
          )
      );
      String htmlBody = templateEngine.process("reset-instructions-template", context);

      helper.setText(htmlBody, true);

      log.info("Sending reset instructions to the specified email: {}",
          emailWithTokenDto.email());
      javaMailSender.send(message);
      log.info("Reset instructions successfully sent to {}", emailWithTokenDto.email());
    } catch (MessagingException | UnsupportedEncodingException e) {
      log.error("Failed to send reset instructions to {}", emailWithTokenDto.email());
      throw new RuntimeException(e);
    }
  }
}
