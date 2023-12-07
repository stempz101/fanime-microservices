package org.stempz.fanime.service.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.stempz.fanime.dto.AuthenticationRequestDto;
import org.stempz.fanime.dto.AuthenticationResponseDto;
import org.stempz.fanime.dto.UserCredentialDto;
import org.stempz.fanime.exception.UserAlreadyExistsException;
import org.stempz.fanime.jwt.JwtService;
import org.stempz.fanime.mapper.UserCredentialMapper;
import org.stempz.fanime.model.UserCredential;
import org.stempz.fanime.repository.UserCredentialRepo;
import org.stempz.fanime.utils.UserCredentialTestUtil;

@ExtendWith(MockitoExtension.class)
public class UserCredentialServiceImplTest {

  @InjectMocks
  private UserCredentialServiceImpl userCredentialService;

  @Mock
  private UserCredentialRepo userCredentialRepo;

  @Mock
  private UserCredentialMapper userCredentialMapper;

  @Mock
  private AuthenticationProvider authenticationProvider;

  @Mock
  private JwtService jwtService;

  @Mock
  private PasswordEncoder passwordEncoder;

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
}
