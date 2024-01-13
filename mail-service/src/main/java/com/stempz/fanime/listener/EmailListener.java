package com.stempz.fanime.listener;

import com.stempz.fanime.dto.EmailWithTokenDto;
import com.stempz.fanime.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailListener {

  private final EmailService emailService;

  @KafkaListener(
      topics = "email-verification-topic",
      groupId = "email-verification-group",
      containerFactory = "emailWithTokenContainerFactory"
  )
  public void emailVerificationListener(EmailWithTokenDto emailWithTokenDto) {
    log.info("Start sending email verification link to user (email={})",
        emailWithTokenDto.email());
    emailService.sendEmailVerificationLink(emailWithTokenDto);
  }

  @KafkaListener(
      topics = "forgot-password-topic",
      groupId = "forgot-password-group",
      containerFactory = "emailWithTokenContainerFactory"
  )
  public void forgotPasswordListener(EmailWithTokenDto emailWithTokenDto) {
    log.info("Start sending reset instructions to user (email={})",
        emailWithTokenDto.email());
    emailService.sendResetInstructions(emailWithTokenDto);
  }

}
