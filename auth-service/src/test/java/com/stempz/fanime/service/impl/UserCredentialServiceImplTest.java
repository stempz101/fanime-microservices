package com.stempz.fanime.service.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import com.stempz.fanime.jwt.JwtService;
import com.stempz.fanime.mapper.UserCredentialMapper;
import com.stempz.fanime.model.PasswordResetToken;
import com.stempz.fanime.model.UserCredential;
import com.stempz.fanime.repository.PasswordResetTokenRepo;
import com.stempz.fanime.repository.UserCredentialRepo;
import com.stempz.fanime.test.utils.PasswordResetTokenTestUtil;
import com.stempz.fanime.test.utils.UserCredentialTestUtil;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class UserCredentialServiceImplTest {

  @InjectMocks
  private UserCredentialServiceImpl userCredentialService;

  @Mock
  private UserCredentialRepo userCredentialRepo;

  @Mock
  private PasswordResetTokenRepo passwordResetTokenRepo;

  @Mock
  private UserCredentialMapper userCredentialMapper;

  @Mock
  private AuthenticationProvider authenticationProvider;

  @Mock
  private JwtService jwtService;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private KafkaTemplate<String, EmailWithTokenDto> emailWithTokenKafkaTemplate;

  @Test
  void authenticate_Success() {
    // Given
    AuthenticationRequestDto authDto = UserCredentialTestUtil.getAuthenticationRequestDto1();
    UserCredential userCredential = UserCredentialTestUtil.getUserCredential1();
    Authentication authentication = mock(Authentication.class);

    AuthenticationResponseDto expectedResult = UserCredentialTestUtil.getAuthenticationResponseDto1();

    // When
    when(authenticationProvider.authenticate(any())).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(userCredential);
    when(jwtService.generateToken(any(), anyLong())).thenReturn(expectedResult.jwt());
    when(userCredentialMapper.mapToAuthenticationResponseDto(any(), anyString(), anyBoolean()))
        .thenReturn(expectedResult);

    AuthenticationResponseDto result = userCredentialService.authenticate(authDto);

    // Then
    assertTrue(result.authenticated());
    verify(authenticationProvider, times(1)).authenticate(any());
    verify(authentication, times(1)).getPrincipal();
    verify(jwtService, times(1)).generateToken(any(), anyLong());
    verify(userCredentialMapper, times(1)).mapToAuthenticationResponseDto(any(), anyString(),
        anyBoolean());

    assertThat(result, samePropertyValuesAs(expectedResult));
  }

  @Test
  void authenticate_BadCredentials_Failure() {
    // Given
    AuthenticationRequestDto authDto = new AuthenticationRequestDto("nonexistent@example.com",
        "password".toCharArray());

    // When
    when(authenticationProvider.authenticate(any())).thenThrow(
        new BadCredentialsException("Invalid credentials"));

    // Then
    assertThrows(BadCredentialsException.class, () -> userCredentialService.authenticate(authDto));
  }

  @Test
  void register_Success() {
    // Given
    UserCredentialDto userCredentialDto = UserCredentialTestUtil.getUserCredentialDto1();
    UserCredential userCredential = UserCredentialTestUtil.getUserCredential1();
    Authentication authentication = mock(Authentication.class);

    AuthenticationResponseDto expectedResult = UserCredentialTestUtil.getAuthenticationResponseDto1();

    // When
    when(userCredentialRepo.existsByEmailIgnoreCase(any())).thenReturn(false);
    when(userCredentialRepo.existsByUsernameIgnoreCase(any())).thenReturn(false);
    when(userCredentialMapper.toUserCredential(any())).thenReturn(mock(UserCredential.class));
    when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
    when(userCredentialRepo.save(any())).thenReturn(userCredential);
    when(authenticationProvider.authenticate(any())).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(userCredential);
    when(jwtService.generateToken(any(), anyLong())).thenReturn(expectedResult.jwt());
    when(userCredentialMapper.mapToAuthenticationResponseDto(any(), anyString(), anyBoolean()))
        .thenReturn(expectedResult);

    AuthenticationResponseDto result = userCredentialService.register(userCredentialDto);

    // Then
    assertTrue(result.authenticated());
    verify(userCredentialRepo, times(1)).existsByEmailIgnoreCase(userCredentialDto.email());
    verify(userCredentialRepo, times(1)).existsByUsernameIgnoreCase(userCredentialDto.username());
    verify(userCredentialMapper, times(1)).toUserCredential(userCredentialDto);
    verify(passwordEncoder, times(1)).encode(anyString());
    verify(userCredentialRepo, times(1)).save(any());
    verify(authenticationProvider, times(1)).authenticate(any());
    verify(authentication, times(1)).getPrincipal();
    verify(jwtService, times(1)).generateToken(any(), anyLong());
    verify(emailWithTokenKafkaTemplate, times(1)).send(any(), any());
    verify(userCredentialMapper, times(1)).mapToAuthenticationResponseDto(any(), anyString(),
        anyBoolean());

    assertThat(result, samePropertyValuesAs(expectedResult));
  }

  @Test
  void register_UserAlreadyExistsByEmail_Failure() {
    // Given
    UserCredentialDto userCredentialDto = UserCredentialTestUtil.getUserCredentialDto1();

    // When
    when(userCredentialRepo.existsByEmailIgnoreCase(any())).thenReturn(true);

    // Then
    assertThrows(UserAlreadyExistsException.class,
        () -> userCredentialService.register(userCredentialDto));
  }

  @Test
  void register_UserAlreadyExistsByUsername_Failure() {
    // Given
    UserCredentialDto userCredentialDto = UserCredentialTestUtil.getUserCredentialDto1();

    // When
    when(userCredentialRepo.existsByEmailIgnoreCase(any())).thenReturn(false);
    when(userCredentialRepo.existsByUsernameIgnoreCase(any())).thenReturn(true);

    // Then
    assertThrows(UserAlreadyExistsException.class,
        () -> userCredentialService.register(userCredentialDto));
  }

  @Test
  void register_BadCredentials_Failure() {
    // Given
    UserCredentialDto userCredentialDto = UserCredentialTestUtil.getUserCredentialDto1();
    UserCredential userCredential = UserCredentialTestUtil.getUserCredential1();

    // When
    when(userCredentialRepo.existsByEmailIgnoreCase(any())).thenReturn(false);
    when(userCredentialRepo.existsByUsernameIgnoreCase(any())).thenReturn(false);
    when(userCredentialMapper.toUserCredential(any())).thenReturn(mock(UserCredential.class));
    when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
    when(userCredentialRepo.save(any())).thenReturn(userCredential);
    when(authenticationProvider.authenticate(any())).thenThrow(
        new BadCredentialsException("Invalid credentials"));

    // Then
    assertThrows(BadCredentialsException.class,
        () -> userCredentialService.register(userCredentialDto));
  }

  @Test
  void verify_Success() {
    // Given
    UserCredential userCredential = UserCredentialTestUtil.getUserCredential2();
    UserCredential userCredentialVerified = UserCredentialTestUtil.getUserCredentialVerified2();

    // When
    when(userCredentialRepo.findByVerificationToken(any())).thenReturn(Optional.of(userCredential));
    when(userCredentialRepo.save(any())).thenReturn(userCredentialVerified);

    userCredentialService.verify(userCredential.getVerificationToken().toString());

    // Then
    verify(userCredentialRepo, times(1)).findByVerificationToken(any());
    verify(userCredentialRepo, times(1)).save(any());
  }

  @Test
  void verify_UserNotFound_Failure() {
    // Given
    UserCredential userCredential = UserCredentialTestUtil.getUserCredential2();

    // When
    when(userCredentialRepo.findByVerificationToken(any())).thenReturn(Optional.empty());

    // Then
    assertThrows(UserNotFoundException.class,
        () -> userCredentialService.verify(userCredential.getVerificationToken().toString()));
  }

  @Test
  void verify_UserAlreadyVerified_Failure() {
    // Given
    UserCredential userCredential = UserCredentialTestUtil.getUserCredential2();
    userCredential.setVerified(true);

    // When
    when(userCredentialRepo.findByVerificationToken(any())).thenReturn(Optional.of(userCredential));

    // Then
    assertThrows(UserAlreadyVerifiedException.class,
        () -> userCredentialService.verify(userCredential.getVerificationToken().toString()));
  }

  @Test
  void validate_AuthenticatedUser_Success() {
    // Given
    UserCredential userCredential = UserCredentialTestUtil.getUserCredential1();
    HttpServletRequest request = mock(HttpServletRequest.class);
    Authentication authentication = mock(Authentication.class);
    SecurityContext securityContext = mock(SecurityContext.class);
    SecurityContextHolder.setContext(securityContext);

    AuthenticationResponseDto expectedResult = UserCredentialTestUtil.getAuthenticationResponseDto1();

    // When
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.isAuthenticated()).thenReturn(true);
    when(authentication.getPrincipal()).thenReturn(userCredential);
    when(jwtService.extractBearerToken(request)).thenReturn(Optional.of(expectedResult.jwt()));
    when(userCredentialMapper.mapToAuthenticationResponseDto(any(), anyString(), anyBoolean()))
        .thenReturn(expectedResult);

    AuthenticationResponseDto result = userCredentialService.validate(request);

    // Then
    assertTrue(result.authenticated());
    verify(securityContext, times(1)).getAuthentication();
    verify(authentication, times(1)).isAuthenticated();
    verify(authentication, times(2)).getPrincipal();
    verify(jwtService, times(1)).extractBearerToken(request);
    verify(userCredentialMapper, times(1)).mapToAuthenticationResponseDto(any(), anyString(),
        anyBoolean());

    assertThat(result, samePropertyValuesAs(expectedResult));
  }

  @Test
  void validate_UnauthenticatedUser_Success() {
    // Given
    HttpServletRequest request = mock(HttpServletRequest.class);
    Authentication authentication = mock(Authentication.class);
    SecurityContext securityContext = mock(SecurityContext.class);
    SecurityContextHolder.setContext(securityContext);

    AuthenticationResponseDto expectedResult = new AuthenticationResponseDto(false);

    // When
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.isAuthenticated()).thenReturn(false);

    AuthenticationResponseDto result = userCredentialService.validate(request);

    // Then
    assertFalse(result.authenticated());
    verify(securityContext, times(1)).getAuthentication();
    verify(authentication, times(1)).isAuthenticated();

    assertThat(result, samePropertyValuesAs(expectedResult));
  }

  @Test
  void validate_AnonymousUser_Success() {
    // Given
    HttpServletRequest request = mock(HttpServletRequest.class);
    Authentication authentication = mock(Authentication.class);
    SecurityContext securityContext = mock(SecurityContext.class);
    SecurityContextHolder.setContext(securityContext);

    AuthenticationResponseDto expectedResult = new AuthenticationResponseDto(false);

    // When
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.isAuthenticated()).thenReturn(true);
    when(authentication.getPrincipal()).thenReturn("anonymousUser");

    AuthenticationResponseDto result = userCredentialService.validate(request);

    // Then
    assertFalse(result.authenticated());
    verify(securityContext, times(1)).getAuthentication();
    verify(authentication, times(1)).isAuthenticated();
    verify(authentication, times(1)).getPrincipal();

    assertThat(result, samePropertyValuesAs(expectedResult));
  }

  @Test
  void forgotPassword_PasswordResetTokenDoesNotExist_Success() {
    // Given
    UserEmailDto userEmailDto = UserCredentialTestUtil.getUserEmailDto1();
    UserCredential userCredential = UserCredentialTestUtil.getUserCredential1();
    PasswordResetToken resetToken = PasswordResetTokenTestUtil.getPasswordResetToken1();

    // When
    when(userCredentialRepo.findByEmailIgnoreCase(any())).thenReturn(Optional.of(userCredential));
    when(passwordResetTokenRepo.findByUserId(anyLong())).thenReturn(Optional.empty());
    when(passwordResetTokenRepo.save(any())).thenReturn(resetToken);

    userCredentialService.forgotPassword(userEmailDto);

    // Then
    verify(userCredentialRepo, times(1)).findByEmailIgnoreCase(any());
    verify(passwordResetTokenRepo, times(1)).findByUserId(anyLong());
    verify(passwordResetTokenRepo, times(1)).save(any());
    verify(emailWithTokenKafkaTemplate, times(1)).send(any(), any());
  }

  @Test
  void forgotPassword_PasswordResetTokenExists_Success() {
    // Given
    UserEmailDto userEmailDto = UserCredentialTestUtil.getUserEmailDto1();
    UserCredential userCredential = UserCredentialTestUtil.getUserCredential1();
    PasswordResetToken resetToken = PasswordResetTokenTestUtil.getPasswordResetToken1();

    // When
    when(userCredentialRepo.findByEmailIgnoreCase(any())).thenReturn(Optional.of(userCredential));
    when(passwordResetTokenRepo.findByUserId(anyLong())).thenReturn(Optional.of(resetToken));
    when(passwordResetTokenRepo.save(any())).thenReturn(resetToken);

    userCredentialService.forgotPassword(userEmailDto);

    // Then
    verify(userCredentialRepo, times(1)).findByEmailIgnoreCase(any());
    verify(passwordResetTokenRepo, times(1)).findByUserId(anyLong());
    verify(passwordResetTokenRepo, times(1)).save(any());
    verify(emailWithTokenKafkaTemplate, times(1)).send(any(), any());
  }

  @Test
  void forgotPassword_UserNotFound_Failure() {
    // Given
    UserEmailDto userEmailDto = UserCredentialTestUtil.getUserEmailDto1();

    // When
    when(userCredentialRepo.findByEmailIgnoreCase(any())).thenReturn(Optional.empty());

    // Then
    assertThrows(UserNotFoundException.class,
        () -> userCredentialService.forgotPassword(userEmailDto));
  }

  @Test
  void resetPassword_Success() {
    // Given
    ResetPasswordDto resetPasswordDto = PasswordResetTokenTestUtil.getResetPasswordDto1();
    PasswordResetToken resetToken = PasswordResetTokenTestUtil.getPasswordResetToken1();
    UserCredential userCredential = UserCredentialTestUtil.getUserCredential1();

    // When
    when(passwordResetTokenRepo.findByToken(any())).thenReturn(Optional.of(resetToken));
    when(userCredentialRepo.save(any())).thenReturn(userCredential);
    doNothing().when(passwordResetTokenRepo).deleteByToken(any());

    userCredentialService.resetPassword(resetPasswordDto);

    // Then
    verify(passwordResetTokenRepo, times(1)).findByToken(any());
    verify(userCredentialRepo, times(1)).save(any());
    verify(passwordResetTokenRepo, times(1)).deleteByToken(any());
  }

  @Test
  void resetPassword_PasswordResetTokenNotFound_Failure() {
    // Given
    ResetPasswordDto resetPasswordDto = PasswordResetTokenTestUtil.getResetPasswordDto1();

    // When
    when(passwordResetTokenRepo.findByToken(any())).thenReturn(Optional.empty());

    // Then
    assertThrows(PasswordResetTokenNotFoundException.class,
        () -> userCredentialService.resetPassword(resetPasswordDto));
  }

  @Test
  void resetPassword_PasswordResetTokenExpired_Failure() {
    // Given
    ResetPasswordDto resetPasswordDto = PasswordResetTokenTestUtil.getResetPasswordDto1();
    PasswordResetToken resetToken = PasswordResetTokenTestUtil.getPasswordResetToken1();
    resetToken.setExpirationTime(resetToken.getExpirationTime().minusDays(10));

    // When
    when(passwordResetTokenRepo.findByToken(any())).thenReturn(Optional.of(resetToken));

    // Then
    assertThrows(PasswordResetTokenExpiredException.class,
        () -> userCredentialService.resetPassword(resetPasswordDto));
  }

  @Test
  void resetPassword_PasswordResetTokenExpired_EdgeCase_Failure() {
    // Given
    ResetPasswordDto resetPasswordDto = PasswordResetTokenTestUtil.getResetPasswordDto1();
    PasswordResetToken resetToken = PasswordResetTokenTestUtil.getPasswordResetToken1();
    resetToken.setExpirationTime(resetToken.getExpirationTime().minusDays(1));

    // When
    when(passwordResetTokenRepo.findByToken(any())).thenReturn(Optional.of(resetToken));

    // Then
    assertThrows(PasswordResetTokenExpiredException.class,
        () -> userCredentialService.resetPassword(resetPasswordDto));
  }
}
