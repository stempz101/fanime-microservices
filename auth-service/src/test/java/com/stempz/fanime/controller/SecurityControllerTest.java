package com.stempz.fanime.controller;

import static com.stempz.fanime.test.utils.ErrorMessageTestUtil.BAD_CREDENTIALS;
import static com.stempz.fanime.test.utils.ErrorMessageTestUtil.EMAIL_NOT_BLANK;
import static com.stempz.fanime.test.utils.ErrorMessageTestUtil.EMAIL_WELL_FORMED;
import static com.stempz.fanime.test.utils.ErrorMessageTestUtil.PASSWORD_INVALID;
import static com.stempz.fanime.test.utils.ErrorMessageTestUtil.PASSWORD_NOT_EMPTY;
import static com.stempz.fanime.test.utils.ErrorMessageTestUtil.USERNAME_NOT_BLANK;
import static com.stempz.fanime.test.utils.ErrorMessageTestUtil.USER_EXISTS_FORMAT;
import static com.stempz.fanime.test.utils.PasswordResetTokenTestUtil.TEST_PRT_TOKEN_1;
import static com.stempz.fanime.test.utils.UserCredentialTestUtil.TEST_USER_EMAIL_1;
import static com.stempz.fanime.test.utils.UserCredentialTestUtil.TEST_USER_ID_1;
import static com.stempz.fanime.test.utils.UserCredentialTestUtil.TEST_USER_JWT_1;
import static com.stempz.fanime.test.utils.UserCredentialTestUtil.TEST_USER_PASSWORD_1;
import static com.stempz.fanime.test.utils.UserCredentialTestUtil.TEST_USER_ROLE_1;
import static com.stempz.fanime.test.utils.UserCredentialTestUtil.TEST_USER_USERNAME_1;
import static com.stempz.fanime.test.utils.UserCredentialTestUtil.getAuthenticationRequestDto1;
import static com.stempz.fanime.test.utils.UserCredentialTestUtil.getAuthenticationRequestDto2;
import static com.stempz.fanime.test.utils.UserCredentialTestUtil.getAuthenticationResponseDto1;
import static com.stempz.fanime.test.utils.UserCredentialTestUtil.getUserCredential1;
import static com.stempz.fanime.test.utils.UserCredentialTestUtil.getUserCredential2;
import static com.stempz.fanime.test.utils.UserCredentialTestUtil.getUserCredentialDto1;
import static com.stempz.fanime.test.utils.UserCredentialTestUtil.getUserCredentialDto2;
import static com.stempz.fanime.test.utils.UserCredentialTestUtil.getUserEmailDto1;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stempz.fanime.config.security.SecurityBeansConfiguration;
import com.stempz.fanime.config.security.SecurityConfiguration;
import com.stempz.fanime.dto.AuthenticationRequestDto;
import com.stempz.fanime.dto.AuthenticationResponseDto;
import com.stempz.fanime.dto.ResetPasswordDto;
import com.stempz.fanime.dto.UserCredentialDto;
import com.stempz.fanime.dto.UserEmailDto;
import com.stempz.fanime.exception.PasswordAlreadyUsedException;
import com.stempz.fanime.exception.PasswordResetTokenExpiredException;
import com.stempz.fanime.exception.PasswordResetTokenNotFoundException;
import com.stempz.fanime.exception.UserAlreadyExistsException;
import com.stempz.fanime.exception.UserAlreadyVerifiedException;
import com.stempz.fanime.exception.UserNotFoundException;
import com.stempz.fanime.exception.enums.UserFieldType;
import com.stempz.fanime.jwt.JwtService;
import com.stempz.fanime.model.UserCredential;
import com.stempz.fanime.repository.UserCredentialRepo;
import com.stempz.fanime.service.UserCredentialService;
import com.stempz.fanime.test.utils.PasswordResetTokenTestUtil;
import com.stempz.fanime.test.utils.UserCredentialTestUtil;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@ExtendWith(SpringExtension.class)
@WebMvcTest(SecurityController.class)
@Import({SecurityBeansConfiguration.class, SecurityConfiguration.class})
public class SecurityControllerTest {

  @MockBean
  private UserCredentialService userCredentialService;

  @MockBean
  private UserCredentialRepo userCredentialRepo;

  @SpyBean
  private JwtService jwtService;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void authenticate_Success() throws Exception {
    // Given
    AuthenticationRequestDto authDto = getAuthenticationRequestDto1();
    UserCredential userCredential = getUserCredential1();

    AuthenticationResponseDto expectedResult = getAuthenticationResponseDto1();

    // When
    when(userCredentialRepo.findByEmailIgnoreCase(any())).thenReturn(Optional.of(userCredential));
    when(userCredentialService.authenticate(any())).thenReturn(expectedResult);

    ResultActions result = mockMvc.perform(post("/api/v1/auth/authenticate")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(authDto)));

    // Then
    result
        .andDo(print())
        .andExpectAll(
            status().isOk(),
            content().contentType(MediaType.APPLICATION_JSON),
            jsonPath("$.authenticated").value(true),
            jsonPath("$.id").value(TEST_USER_ID_1),
            jsonPath("$.email").value(TEST_USER_EMAIL_1),
            jsonPath("$.username").value(TEST_USER_USERNAME_1),
            jsonPath("$.jwt").value(TEST_USER_JWT_1),
            jsonPath("$.role").value(TEST_USER_ROLE_1.name())
        );
  }

  @Test
  void authenticate_BadCredentials_Failure() throws Exception {
    // Given
    AuthenticationRequestDto authDto = getAuthenticationRequestDto2();
    UserCredential userCredential = getUserCredential1();

    // When
    when(userCredentialRepo.findByEmailIgnoreCase(any())).thenReturn(Optional.of(userCredential));
    when(userCredentialService.authenticate(any())).thenThrow(
        new BadCredentialsException(BAD_CREDENTIALS));

    ResultActions result = mockMvc.perform(post("/api/v1/auth/authenticate")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(authDto)));

    // Then
    result
        .andDo(print())
        .andExpectAll(
            status().isUnauthorized(),
            content().contentType(MediaType.APPLICATION_JSON),
            jsonPath("$").value(hasSize(1)),
            jsonPath("$[0].message").value(BAD_CREDENTIALS)
        );
  }

  @Test
  void register_Success() throws Exception {
    // Given
    UserCredentialDto userCredentialDto = getUserCredentialDto1();
    AuthenticationResponseDto expectedResult = getAuthenticationResponseDto1();

    // When
    when(userCredentialService.register(any())).thenReturn(expectedResult);

    ResultActions result = mockMvc.perform(post("/api/v1/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(userCredentialDto)));

    // Then
    result
        .andDo(print())
        .andExpectAll(
            status().isOk(),
            content().contentType(MediaType.APPLICATION_JSON),
            jsonPath("$.authenticated").value(true),
            jsonPath("$.id").value(TEST_USER_ID_1),
            jsonPath("$.email").value(TEST_USER_EMAIL_1),
            jsonPath("$.username").value(TEST_USER_USERNAME_1),
            jsonPath("$.jwt").value(TEST_USER_JWT_1),
            jsonPath("$.role").value(TEST_USER_ROLE_1.name())
        );
  }

  @Test
  void register_AllFieldsNull_Failure() throws Exception {
    // Given
    UserCredentialDto userCredentialDto = new UserCredentialDto(null, null, null);

    // When
    ResultActions result = mockMvc.perform(post("/api/v1/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(userCredentialDto)));

    // Then
    result
        .andDo(print())
        .andExpectAll(
            status().isBadRequest(),
            content().contentType(MediaType.APPLICATION_JSON),
            jsonPath("$").value(hasSize(3)),
            jsonPath("$[*].message").value(containsInAnyOrder(
                EMAIL_NOT_BLANK,
                USERNAME_NOT_BLANK,
                PASSWORD_NOT_EMPTY
            ))
        );
  }

  @Test
  void register_AllFieldsBlank_Failure() throws Exception {
    // Given
    UserCredentialDto userCredentialDto = new UserCredentialDto("", "", new char[0]);

    // When
    ResultActions result = mockMvc.perform(post("/api/v1/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(userCredentialDto)));

    // Then
    result
        .andDo(print())
        .andExpectAll(
            status().isBadRequest(),
            content().contentType(MediaType.APPLICATION_JSON),
            jsonPath("$").value(hasSize(3)),
            jsonPath("$[*].message").value(containsInAnyOrder(
                EMAIL_NOT_BLANK,
                USERNAME_NOT_BLANK,
                PASSWORD_NOT_EMPTY
            ))
        );
  }

  @Test
  void register_InvalidEmail_Failure() throws Exception {
    // Given
    UserCredentialDto userCredentialDto = new UserCredentialDto("testgmail.com",
        TEST_USER_USERNAME_1, TEST_USER_PASSWORD_1.toCharArray());

    // When
    ResultActions result = mockMvc.perform(post("/api/v1/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(userCredentialDto)));

    // Then
    result
        .andDo(print())
        .andExpectAll(
            status().isBadRequest(),
            content().contentType(MediaType.APPLICATION_JSON),
            jsonPath("$").value(hasSize(1)),
            jsonPath("$[0].message").value(EMAIL_WELL_FORMED)
        );
  }

  @Test
  void register_InvalidPassword_LessThan8Symbols_Failure() throws Exception {
    // Given
    UserCredentialDto userCredentialDto = new UserCredentialDto(TEST_USER_EMAIL_1,
        TEST_USER_USERNAME_1, "qwe1234".toCharArray());

    // When
    ResultActions result = mockMvc.perform(post("/api/v1/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(userCredentialDto)));

    // Then
    result
        .andDo(print())
        .andExpectAll(
            status().isBadRequest(),
            content().contentType(MediaType.APPLICATION_JSON),
            jsonPath("$").value(hasSize(1)),
            jsonPath("$[0].message").value(PASSWORD_INVALID)
        );
  }

  @Test
  void register_InvalidPassword_WithoutLetters_Failure() throws Exception {
    // Given
    UserCredentialDto userCredentialDto = new UserCredentialDto(TEST_USER_EMAIL_1,
        TEST_USER_USERNAME_1, "190853654".toCharArray());

    // When
    ResultActions result = mockMvc.perform(post("/api/v1/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(userCredentialDto)));

    // Then
    result
        .andDo(print())
        .andExpectAll(
            status().isBadRequest(),
            content().contentType(MediaType.APPLICATION_JSON),
            jsonPath("$").value(hasSize(1)),
            jsonPath("$[0].message").value(PASSWORD_INVALID)
        );
  }

  @Test
  void register_InvalidPassword_WithoutDigits_Failure() throws Exception {
    // Given
    UserCredentialDto userCredentialDto = new UserCredentialDto(TEST_USER_EMAIL_1,
        TEST_USER_USERNAME_1, "asdfghjzxcv".toCharArray());

    // When
    ResultActions result = mockMvc.perform(post("/api/v1/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(userCredentialDto)));

    // Then
    result
        .andDo(print())
        .andExpectAll(
            status().isBadRequest(),
            content().contentType(MediaType.APPLICATION_JSON),
            jsonPath("$").value(hasSize(1)),
            jsonPath("$[0].message").value(PASSWORD_INVALID)
        );
  }

  @Test
  void register_ExistsByEmail_Failure() throws Exception {
    // Given
    UserCredentialDto userCredentialDto = getUserCredentialDto2();
    String expectedErrorMessage = String.format(USER_EXISTS_FORMAT,
        UserFieldType.EMAIL.name().toLowerCase(), userCredentialDto.email());

    // When
    when(userCredentialService.register(any())).thenThrow(
        new UserAlreadyExistsException(userCredentialDto.email(), UserFieldType.EMAIL));

    ResultActions result = mockMvc.perform(post("/api/v1/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(userCredentialDto)));

    // Then
    result
        .andDo(print())
        .andExpectAll(
            status().isBadRequest(),
            content().contentType(MediaType.APPLICATION_JSON),
            jsonPath("$").value(hasSize(1)),
            jsonPath("$[0].message").value(expectedErrorMessage)
        );
  }

  @Test
  void register_ExistsByUsername_Failure() throws Exception {
    // Given
    UserCredentialDto userCredentialDto = getUserCredentialDto2();
    String expectedErrorMessage = String.format(USER_EXISTS_FORMAT,
        UserFieldType.USERNAME.name().toLowerCase(), userCredentialDto.username());

    // When
    when(userCredentialService.register(any())).thenThrow(
        new UserAlreadyExistsException(userCredentialDto.username(), UserFieldType.USERNAME));

    ResultActions result = mockMvc.perform(post("/api/v1/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(userCredentialDto)));

    // Then
    result
        .andDo(print())
        .andExpectAll(
            status().isBadRequest(),
            content().contentType(MediaType.APPLICATION_JSON),
            jsonPath("$").value(hasSize(1)),
            jsonPath("$[0].message").value(expectedErrorMessage)
        );
  }

  @Test
  void verify_Success() throws Exception {
    // Given
    UserCredential userCredential = getUserCredential1();

    // When
    doNothing().when(userCredentialService)
        .verify(userCredential.getVerificationToken().toString());

    ResultActions result = mockMvc.perform(get("/api/v1/auth/verify")
        .param("token", userCredential.getVerificationToken().toString()));

    // Then
    result
        .andDo(print())
        .andExpect(status().isOk());
  }

  @Test
  void verify_WithoutTokenParam_Failure() throws Exception {
    // Given
    UserCredential userCredential = getUserCredential2();

    // When
    doNothing().when(userCredentialService)
        .verify(userCredential.getVerificationToken().toString());

    ResultActions result = mockMvc.perform(get("/api/v1/auth/verify"));

    // Then
    result
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  void verify_UserNotFound_Failure() throws Exception {
    // Given
    UserCredential userCredential = getUserCredential2();

    // When
    doThrow(new UserNotFoundException(userCredential.getVerificationToken().toString(), UserFieldType.TOKEN))
        .when(userCredentialService).verify(userCredential.getVerificationToken().toString());

    ResultActions result = mockMvc.perform(get("/api/v1/auth/verify")
        .param("token", userCredential.getVerificationToken().toString()));

    // Then
    result
        .andDo(print())
        .andExpectAll(
            status().isNotFound(),
            content().contentType(MediaType.APPLICATION_JSON),
            jsonPath("$").value(hasSize(1)),
            jsonPath("$[0].message")
                .value(String.format("User is not found by token: %s",
                    userCredential.getVerificationToken().toString()))
        );
  }

  @Test
  void verify_UserAlreadyVerified_Failure() throws Exception {
    // Given
    UserCredential userCredential = getUserCredential1();

    // When
    doThrow(new UserAlreadyVerifiedException(userCredential.get_username()))
        .when(userCredentialService).verify(userCredential.getVerificationToken().toString());

    ResultActions result = mockMvc.perform(get("/api/v1/auth/verify")
        .param("token", userCredential.getVerificationToken().toString()));

    // Then
    result
        .andDo(print())
        .andExpectAll(
            status().isBadRequest(),
            content().contentType(MediaType.APPLICATION_JSON),
            jsonPath("$").value(hasSize(1)),
            jsonPath("$[0].message")
                .value(String.format("User with username \"%s\" is already verified",
                    userCredential.get_username()))
        );
  }

  @Test
  void validateToken_Authenticated_Success() throws Exception {
    // Given
    AuthenticationResponseDto expectedResult = getAuthenticationResponseDto1();
    UserCredential userCredential = getUserCredential1();

    // When
    when(userCredentialRepo.findByEmailIgnoreCase(any())).thenReturn(Optional.of(userCredential));
    when(userCredentialService.validate(any())).thenReturn(expectedResult);

    ResultActions result = mockMvc.perform(get("/api/v1/auth/validate")
        .contentType(MediaType.APPLICATION_JSON)
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TEST_USER_JWT_1));

    // Then
    result
        .andDo(print())
        .andExpectAll(
            status().isOk(),
            content().contentType(MediaType.APPLICATION_JSON),
            jsonPath("$.authenticated").value(true),
            jsonPath("$.id").value(TEST_USER_ID_1),
            jsonPath("$.email").value(TEST_USER_EMAIL_1),
            jsonPath("$.username").value(TEST_USER_USERNAME_1),
            jsonPath("$.jwt").value(TEST_USER_JWT_1),
            jsonPath("$.role").value(TEST_USER_ROLE_1.name())
        );
  }

  @Test
  void validateToken_Unauthenticated_Success() throws Exception {
    // Given
    AuthenticationResponseDto expectedResult = new AuthenticationResponseDto(false);
    UserCredential userCredential = getUserCredential1();

    // When
    when(userCredentialRepo.findByEmailIgnoreCase(any())).thenReturn(Optional.of(userCredential));
    when(userCredentialService.validate(any())).thenReturn(expectedResult);

    ResultActions result = mockMvc.perform(get("/api/v1/auth/validate")
        .contentType(MediaType.APPLICATION_JSON));

    // Then
    result
        .andDo(print())
        .andExpectAll(
            status().isOk(),
            content().contentType(MediaType.APPLICATION_JSON),
            jsonPath("$.authenticated").value(false),
            jsonPath("$.id").doesNotExist(),
            jsonPath("$.email").doesNotExist(),
            jsonPath("$.username").doesNotExist(),
            jsonPath("$.jwt").doesNotExist(),
            jsonPath("$.role").doesNotExist()
        );
  }

  @Test
  void forgotPassword_Success() throws Exception {
    // Given
    UserEmailDto userEmailDto = getUserEmailDto1();

    // When
    doNothing().when(userCredentialService).forgotPassword(any());

    ResultActions result = mockMvc.perform(post("/api/v1/auth/forgot-password")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(userEmailDto)));

    // Then
    result
        .andDo(print())
        .andExpect(status().isOk());
  }

  @Test
  void forgotPassword_EmailIsBlank_Failure() throws Exception {
    // Given
    UserEmailDto userEmailDto = new UserEmailDto("");

    // When
    ResultActions result = mockMvc.perform(post("/api/v1/auth/forgot-password")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(userEmailDto)));

    // Then
    result
        .andDo(print())
        .andExpectAll(
            status().isBadRequest(),
            content().contentType(MediaType.APPLICATION_JSON),
            jsonPath("$").value(hasSize(1)),
            jsonPath("$[0].message").value("Email must not be empty")
        );
  }

  @Test
  void forgotPassword_InvalidEmail_Failure() throws Exception {
    // Given
    UserEmailDto userEmailDto = new UserEmailDto("testgmail.com");

    // When
    ResultActions result = mockMvc.perform(post("/api/v1/auth/forgot-password")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(userEmailDto)));

    // Then
    result
        .andDo(print())
        .andExpectAll(
            status().isBadRequest(),
            content().contentType(MediaType.APPLICATION_JSON),
            jsonPath("$").value(hasSize(1)),
            jsonPath("$[0].message")
                .value("Email address must be well-formed")
        );
  }

  @Test
  void forgotPassword_UserNotFound_Failure() throws Exception {
    // Given
    UserEmailDto userEmailDto = getUserEmailDto1();

    // When
    doThrow(new UserNotFoundException(userEmailDto.email(), UserFieldType.EMAIL))
        .when(userCredentialService).forgotPassword(any());

    ResultActions result = mockMvc.perform(post("/api/v1/auth/forgot-password")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(userEmailDto)));

    // Then
    result
        .andDo(print())
        .andExpectAll(
            status().isNotFound(),
            content().contentType(MediaType.APPLICATION_JSON),
            jsonPath("$").value(hasSize(1)),
            jsonPath("$[0].message")
                .value(String.format("User is not found by %s: %s",
                    UserFieldType.EMAIL.name().toLowerCase(), userEmailDto.email()))
        );
  }

  @Test
  void resetPassword_Success() throws Exception {
    // Given
    ResetPasswordDto resetPasswordDto = PasswordResetTokenTestUtil.getResetPasswordDto1();

    // When
    doNothing().when(userCredentialService).resetPassword(any());

    ResultActions result = mockMvc.perform(post("/api/v1/auth/reset-password")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(resetPasswordDto)));

    // Then
    result
        .andDo(print())
        .andExpect(status().isOk());
  }

  @Test
  void resetPassword_PasswordIsNull_Failure() throws Exception {
    // Given
    ResetPasswordDto resetPasswordDto =
        new ResetPasswordDto(null, TEST_PRT_TOKEN_1.toString());

    // When
    ResultActions result = mockMvc.perform(post("/api/v1/auth/reset-password")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(resetPasswordDto)));

    // Then
    result
        .andDo(print())
        .andExpectAll(
            status().isBadRequest(),
            content().contentType(MediaType.APPLICATION_JSON),
            jsonPath("$").value(hasSize(1)),
            jsonPath("$[0].message").value("Password must not be empty")
        );
  }

  @Test
  void resetPassword_PasswordIsEmpty_Failure() throws Exception {
    // Given
    ResetPasswordDto resetPasswordDto =
        new ResetPasswordDto("".toCharArray(), TEST_PRT_TOKEN_1.toString());

    // When
    ResultActions result = mockMvc.perform(post("/api/v1/auth/reset-password")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(resetPasswordDto)));

    // Then
    result
        .andDo(print())
        .andExpectAll(
            status().isBadRequest(),
            content().contentType(MediaType.APPLICATION_JSON),
            jsonPath("$").value(hasSize(1)),
            jsonPath("$[0].message").value("Password must not be empty")
        );
  }

  @Test
  void resetPassword_PasswordIsLessThan8Symbols_Failure() throws Exception {
    // Given
    ResetPasswordDto resetPasswordDto =
        new ResetPasswordDto("test123".toCharArray(), TEST_PRT_TOKEN_1.toString());

    // When
    ResultActions result = mockMvc.perform(post("/api/v1/auth/reset-password")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(resetPasswordDto)));

    // Then
    result
        .andDo(print())
        .andExpectAll(
            status().isBadRequest(),
            content().contentType(MediaType.APPLICATION_JSON),
            jsonPath("$").value(hasSize(1)),
            jsonPath("$[0].message")
                .value("Password must contain 8 or more symbols, at least one letter and one digit")
        );
  }

  @Test
  void resetPassword_PasswordWithoutLetters_Failure() throws Exception {
    // Given
    ResetPasswordDto resetPasswordDto =
        new ResetPasswordDto("876234523".toCharArray(), TEST_PRT_TOKEN_1.toString());

    // When
    ResultActions result = mockMvc.perform(post("/api/v1/auth/reset-password")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(resetPasswordDto)));

    // Then
    result
        .andDo(print())
        .andExpectAll(
            status().isBadRequest(),
            content().contentType(MediaType.APPLICATION_JSON),
            jsonPath("$").value(hasSize(1)),
            jsonPath("$[0].message")
                .value("Password must contain 8 or more symbols, at least one letter and one digit")
        );
  }

  @Test
  void resetPassword_PasswordWithoutDigits_Failure() throws Exception {
    // Given
    ResetPasswordDto resetPasswordDto =
        new ResetPasswordDto("iuyasdhdjd".toCharArray(), TEST_PRT_TOKEN_1.toString());

    // When
    ResultActions result = mockMvc.perform(post("/api/v1/auth/reset-password")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(resetPasswordDto)));

    // Then
    result
        .andDo(print())
        .andExpectAll(
            status().isBadRequest(),
            content().contentType(MediaType.APPLICATION_JSON),
            jsonPath("$").value(hasSize(1)),
            jsonPath("$[0].message")
                .value("Password must contain 8 or more symbols, at least one letter and one digit")
        );
  }

  @Test
  void resetPassword_PasswordResetTokenNotFound_Failure() throws Exception {
    // Given
    ResetPasswordDto resetPasswordDto = PasswordResetTokenTestUtil.getResetPasswordDto1();;

    // When
    doThrow(new PasswordResetTokenNotFoundException())
        .when(userCredentialService).resetPassword(any());

    ResultActions result = mockMvc.perform(post("/api/v1/auth/reset-password")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(resetPasswordDto)));

    // Then
    result
        .andDo(print())
        .andExpectAll(
            status().isNotFound(),
            content().contentType(MediaType.APPLICATION_JSON),
            jsonPath("$").value(hasSize(1)),
            jsonPath("$[0].message").value("Password reset token is not found")
        );
  }

  @Test
  void resetPassword_PasswordResetTokenExpired_Failure() throws Exception {
    // Given
    ResetPasswordDto resetPasswordDto = PasswordResetTokenTestUtil.getResetPasswordDto1();;

    // When
    doThrow(new PasswordResetTokenExpiredException())
        .when(userCredentialService).resetPassword(any());

    ResultActions result = mockMvc.perform(post("/api/v1/auth/reset-password")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(resetPasswordDto)));

    // Then
    result
        .andDo(print())
        .andExpectAll(
            status().isBadRequest(),
            content().contentType(MediaType.APPLICATION_JSON),
            jsonPath("$").value(hasSize(1)),
            jsonPath("$[0].message").value("Password reset token is already expired")
        );
  }

  @Test
  void resetPassword_PasswordAlreadyUsed_Failure() throws Exception {
    // Given
    ResetPasswordDto resetPasswordDto = PasswordResetTokenTestUtil.getResetPasswordDto1();;

    // When
    doThrow(new PasswordAlreadyUsedException())
        .when(userCredentialService).resetPassword(any());

    ResultActions result = mockMvc.perform(post("/api/v1/auth/reset-password")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(resetPasswordDto)));

    // Then
    result
        .andDo(print())
        .andExpectAll(
            status().isBadRequest(),
            content().contentType(MediaType.APPLICATION_JSON),
            jsonPath("$").value(hasSize(1)),
            jsonPath("$[0].message")
                .value("Entered new password was previously used or is already in use. Please choose a different password")
        );
  }
}
