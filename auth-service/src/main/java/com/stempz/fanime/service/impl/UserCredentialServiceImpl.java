package com.stempz.fanime.service.impl;

import com.stempz.fanime.dto.AuthenticationRequestDto;
import com.stempz.fanime.dto.AuthenticationResponseDto;
import com.stempz.fanime.dto.EmailWithTokenDto;
import com.stempz.fanime.dto.ResetPasswordDto;
import com.stempz.fanime.dto.UserCredentialDto;
import com.stempz.fanime.dto.UserEmailDto;
import com.stempz.fanime.exception.PasswordResetTokenExpiredException;
import com.stempz.fanime.exception.PasswordResetTokenNotFoundException;
import com.stempz.fanime.exception.UserAlreadyVerifiedException;
import com.stempz.fanime.exception.UserAlreadyExistsException;
import com.stempz.fanime.exception.UserNotFoundException;
import com.stempz.fanime.exception.enums.UserFieldType;
import com.stempz.fanime.jwt.JwtService;
import com.stempz.fanime.mapper.UserCredentialMapper;
import com.stempz.fanime.model.PasswordResetToken;
import com.stempz.fanime.model.UserCredential;
import com.stempz.fanime.model.enums.Role;
import com.stempz.fanime.repository.PasswordResetTokenRepo;
import com.stempz.fanime.repository.UserCredentialRepo;
import com.stempz.fanime.service.UserCredentialService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;
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
  private final PasswordResetTokenRepo passwordResetTokenRepo;

  private final UserCredentialMapper userCredentialMapper;

  private final AuthenticationProvider authenticationProvider;
  private final JwtService jwtService;
  private final PasswordEncoder passwordEncoder;

  private final KafkaTemplate<String, EmailWithTokenDto> emailWithTokenKafkaTemplate;

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
      throw new UserAlreadyExistsException(userCredentialDto.email(), UserFieldType.EMAIL);
    } else if (userCredentialRepo.existsByUsernameIgnoreCase(userCredentialDto.username())) {
      throw new UserAlreadyExistsException(userCredentialDto.username(), UserFieldType.USERNAME);
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
    emailWithTokenKafkaTemplate.send("email-verification-topic",
        new EmailWithTokenDto(userCredential.getEmail(), userCredential.getVerificationToken()));

    return userCredentialMapper.mapToAuthenticationResponseDto(userCredential, jwt, true);
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

  @Override
  public void verify(String token) {
    log.info("Email verification by token: {}", token);

    UserCredential userCredential = userCredentialRepo.findByVerificationToken(UUID.fromString(token))
        .orElseThrow(() -> new UserNotFoundException(token, UserFieldType.TOKEN));

    if (userCredential.isVerified()) {
      throw new UserAlreadyVerifiedException(userCredential.get_username());
    }

    userCredential.setVerified(true);
    userCredentialRepo.save(userCredential);

    log.info("Email successfully verified by specified token: {}", token);
  }

  @Override
  public void forgotPassword(UserEmailDto userEmailDto) {
    log.info("User (email={}) forgot password", userEmailDto.email());

    UserCredential userCredential = userCredentialRepo.findByEmailIgnoreCase(userEmailDto.email())
        .orElseThrow(() -> new UserNotFoundException(userEmailDto.email(), UserFieldType.EMAIL));
    Optional<PasswordResetToken> passwordResetTokenOpt = passwordResetTokenRepo
        .findByUserId(userCredential.getId());

    PasswordResetToken passwordResetToken;
    if (passwordResetTokenOpt.isEmpty()) {
      passwordResetToken = new PasswordResetToken();
      passwordResetToken.setUser(userCredential);
    } else {
      passwordResetToken = passwordResetTokenOpt.get();
    }
    passwordResetToken.setToken(UUID.randomUUID());
    passwordResetToken.setExpirationTime(LocalDateTime.now().plusDays(1));

    passwordResetToken = passwordResetTokenRepo.save(passwordResetToken);

    log.info("Sending password reset instructions to the user (email={})", userEmailDto.email());
    emailWithTokenKafkaTemplate.send("forgot-password-topic",
        new EmailWithTokenDto(userCredential.getEmail(), passwordResetToken.getToken()));
  }

  @Override
  @Transactional
  public void resetPassword(ResetPasswordDto resetPasswordDto) {
    log.info("User trying to reset password by token: {}", resetPasswordDto.token());

    PasswordResetToken passwordResetToken = passwordResetTokenRepo
        .findByToken(UUID.fromString(resetPasswordDto.token()))
        .orElseThrow(PasswordResetTokenNotFoundException::new);

    if (LocalDateTime.now().isAfter(passwordResetToken.getExpirationTime())) {
      throw new PasswordResetTokenExpiredException();
    }

    UserCredential userCredential = passwordResetToken.getUser();
    userCredential.setPassword(encodePassword(resetPasswordDto.password()));
    userCredentialRepo.save(userCredential);

    passwordResetTokenRepo.deleteByToken(passwordResetToken.getToken());

    log.info("User (email={}) successfully reset his password", userCredential.getEmail());
  }

  private String encodePassword(char[] password) {
    return passwordEncoder.encode(String.valueOf(password));
  }
}
