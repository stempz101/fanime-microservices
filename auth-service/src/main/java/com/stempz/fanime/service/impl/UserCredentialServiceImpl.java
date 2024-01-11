package com.stempz.fanime.service.impl;

import com.stempz.fanime.dto.AuthenticationRequestDto;
import com.stempz.fanime.dto.AuthenticationResponseDto;
import com.stempz.fanime.dto.EmailVerificationDto;
import com.stempz.fanime.dto.UserCredentialDto;
import com.stempz.fanime.exception.UserAlreadyVerifiedException;
import com.stempz.fanime.exception.UserAlreadyExistsException;
import com.stempz.fanime.exception.UserAlreadyExistsException.FieldType;
import com.stempz.fanime.exception.UserNotFoundException;
import com.stempz.fanime.jwt.JwtService;
import com.stempz.fanime.mapper.UserCredentialMapper;
import com.stempz.fanime.model.UserCredential;
import com.stempz.fanime.model.enums.Role;
import com.stempz.fanime.repository.UserCredentialRepo;
import com.stempz.fanime.service.UserCredentialService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserCredentialServiceImpl implements UserCredentialService {

  private final UserCredentialRepo userCredentialRepo;
  private final UserCredentialMapper userCredentialMapper;

  private final AuthenticationProvider authenticationProvider;
  private final JwtService jwtService;
  private final PasswordEncoder passwordEncoder;

  private final KafkaTemplate<String, EmailVerificationDto> emailVerificationKafkaTemplate;

  @Override
  public AuthenticationResponseDto authenticate(AuthenticationRequestDto authDto) {
    log.info("User (email={}) authentication", authDto.email());

    UserCredential userCredential = (UserCredential) authenticate(authDto.email(),
        authDto.password());

    String jwt = jwtService.generateToken(userCredential, userCredential.getId());
    return userCredentialMapper.mapToAuthenticationResponseDto(userCredential, jwt, true);
  }

  private UserDetails authenticate(String username, char[] password) {
    log.info("Start user (email={}) authentication", username);

    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
        username, String.valueOf(password));
    Authentication authentication = authenticationProvider.authenticate(authToken);
    SecurityContextHolder.getContext().setAuthentication(authentication);

    log.info("User (email={}) successfully authenticated", username);
    return (UserDetails) authentication.getPrincipal();
  }

  @Override
  public AuthenticationResponseDto register(UserCredentialDto userCredentialDto) {
    log.info("User (email={}, username={}) registration", userCredentialDto.email(),
        userCredentialDto.username());

    if (userCredentialRepo.existsByEmailIgnoreCase(userCredentialDto.email())) {
      throw new UserAlreadyExistsException(userCredentialDto.email(), FieldType.EMAIL);
    } else if (userCredentialRepo.existsByUsernameIgnoreCase(userCredentialDto.username())) {
      throw new UserAlreadyExistsException(userCredentialDto.username(), FieldType.USERNAME);
    }

    UserCredential userCredential = userCredentialMapper.toUserCredential(userCredentialDto);
    userCredential.setPassword(encodePassword(userCredentialDto.password()));
    userCredential.setRole(Role.USER);
    userCredential.setVerificationToken(UUID.randomUUID());

    userCredential = userCredentialRepo.save(userCredential);
    log.info("User (email={}, username={}) successfully registered", userCredentialDto.email(),
        userCredentialDto.username());

    authenticate(userCredentialDto.email(), userCredentialDto.password());
    String jwt = jwtService.generateToken(userCredential, userCredential.getId());

    log.info("Sending email verification link to the new user (email={}, username={})",
        userCredentialDto.email(), userCredentialDto.username());
    emailVerificationKafkaTemplate.send("email-verification-topic",
        new EmailVerificationDto(userCredential.getEmail(), userCredential.getVerificationToken()));

    return userCredentialMapper.mapToAuthenticationResponseDto(userCredential, jwt, true);
  }

  @Override
  public void verify(String token) {
    log.info("Email verification by token: {}", token);

    UserCredential userCredential = userCredentialRepo.findByVerificationToken(UUID.fromString(token))
        .orElseThrow(() -> new UserNotFoundException(token));

    if (userCredential.isVerified()) {
      throw new UserAlreadyVerifiedException(userCredential.get_username());
    }

    userCredential.setVerified(true);
    userCredentialRepo.save(userCredential);

    log.info("Email successfully verified by specified token: {}", token);
  }

  @Override
  public AuthenticationResponseDto validate(HttpServletRequest request) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!authentication.isAuthenticated() || authentication.getPrincipal()
        .equals("anonymousUser")) {
      return new AuthenticationResponseDto(false);
    }

    String token = jwtService.extractBearerToken(request).get();
    UserCredential userCredential = (UserCredential) authentication.getPrincipal();
    return userCredentialMapper.mapToAuthenticationResponseDto(userCredential, token, true);
  }

  private String encodePassword(char[] password) {
    return passwordEncoder.encode(String.valueOf(password));
  }
}
