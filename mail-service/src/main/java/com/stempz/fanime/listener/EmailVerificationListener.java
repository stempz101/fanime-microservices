package com.stempz.fanime.listener;

import com.stempz.fanime.dto.EmailVerificationDto;
import com.stempz.fanime.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailVerificationListener {

  private final EmailService emailService;

  @KafkaListener(
      topics = "email-verification-topic",
      groupId = "email-verification-group",
      containerFactory = "emailVerificationContainerFactory"
  )
  public void listener(EmailVerificationDto emailVerificationDto) {
    log.info("Start sending email verification link to user (email={})",
        emailVerificationDto.email());
    emailService.sendEmailVerificationLink(emailVerificationDto);
  }

}
