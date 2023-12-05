package org.stempz.fanime.controller;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.stempz.fanime.utils.ErrorMessageTestUtil.BAD_CREDENTIALS;
import static org.stempz.fanime.utils.ErrorMessageTestUtil.EMAIL_NOT_BLANK;
import static org.stempz.fanime.utils.ErrorMessageTestUtil.EMAIL_WELL_FORMED;
import static org.stempz.fanime.utils.ErrorMessageTestUtil.PASSWORD_INVALID;
import static org.stempz.fanime.utils.ErrorMessageTestUtil.PASSWORD_NOT_EMPTY;
import static org.stempz.fanime.utils.ErrorMessageTestUtil.USERNAME_NOT_BLANK;
import static org.stempz.fanime.utils.ErrorMessageTestUtil.USER_EXISTS_FORMAT;
import static org.stempz.fanime.utils.UserCredentialTestUtil.TEST_USER_EMAIL_1;
import static org.stempz.fanime.utils.UserCredentialTestUtil.TEST_USER_ID_1;
import static org.stempz.fanime.utils.UserCredentialTestUtil.TEST_USER_JWT_1;
import static org.stempz.fanime.utils.UserCredentialTestUtil.TEST_USER_PASSWORD_1;
import static org.stempz.fanime.utils.UserCredentialTestUtil.TEST_USER_ROLE_1;
import static org.stempz.fanime.utils.UserCredentialTestUtil.TEST_USER_USERNAME_1;
import static org.stempz.fanime.utils.UserCredentialTestUtil.getAuthenticationRequestDto1;
import static org.stempz.fanime.utils.UserCredentialTestUtil.getAuthenticationRequestDto2;
import static org.stempz.fanime.utils.UserCredentialTestUtil.getAuthenticationResponseDto1;
import static org.stempz.fanime.utils.UserCredentialTestUtil.getUserCredentialDto1;
import static org.stempz.fanime.utils.UserCredentialTestUtil.getUserCredentialDto2;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.stempz.fanime.dto.AuthenticationRequestDto;
import org.stempz.fanime.dto.AuthenticationResponseDto;
import org.stempz.fanime.dto.UserCredentialDto;
import org.stempz.fanime.exception.UserAlreadyExistsException;
import org.stempz.fanime.exception.UserAlreadyExistsException.FieldType;
import org.stempz.fanime.jwt.JwtService;
import org.stempz.fanime.service.UserCredentialService;

@ExtendWith(SpringExtension.class)
@WebMvcTest(value = SecurityController.class)
public class SecurityControllerTest {

  @MockBean
  private UserCredentialService userCredentialService;

  @MockBean
  JwtService jwtService;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  @WithMockUser
  void authenticate_Success() throws Exception {
    // Given
    AuthenticationRequestDto authDto = getAuthenticationRequestDto1();
    AuthenticationResponseDto expectedResult = getAuthenticationResponseDto1();

    // When
    when(userCredentialService.authenticate(any())).thenReturn(expectedResult);

    ResultActions result = mockMvc.perform(post("/api/v1/auth/authenticate")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(authDto))
        .with(csrf()));

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
  @WithMockUser
  void authenticate_BadCredentials_Failure() throws Exception {
    // Given
    AuthenticationRequestDto authDto = getAuthenticationRequestDto2();

    // When
    when(userCredentialService.authenticate(any())).thenThrow(
        new BadCredentialsException(BAD_CREDENTIALS));

    ResultActions result = mockMvc.perform(post("/api/v1/auth/authenticate")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(authDto))
        .with(csrf()));

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
  @WithMockUser
  void register_Success() throws Exception {
    // Given
    UserCredentialDto userCredentialDto = getUserCredentialDto1();
    AuthenticationResponseDto expectedResult = getAuthenticationResponseDto1();

    // When
    when(userCredentialService.register(any())).thenReturn(expectedResult);

    ResultActions result = mockMvc.perform(post("/api/v1/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(userCredentialDto))
        .with(csrf()));

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
  @WithMockUser
  void register_AllFieldsNull_Failure() throws Exception {
    // Given
    UserCredentialDto userCredentialDto = new UserCredentialDto(null, null, null);

    // When
    ResultActions result = mockMvc.perform(post("/api/v1/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(userCredentialDto))
        .with(csrf()));

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
  @WithMockUser
  void register_AllFieldsBlank_Failure() throws Exception {
    // Given
    UserCredentialDto userCredentialDto = new UserCredentialDto("", "", new char[0]);

    // When
    ResultActions result = mockMvc.perform(post("/api/v1/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(userCredentialDto))
        .with(csrf()));

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
  @WithMockUser
  void register_InvalidEmail_Failure() throws Exception {
    // Given
    UserCredentialDto userCredentialDto = new UserCredentialDto("testgmail.com",
        TEST_USER_USERNAME_1, TEST_USER_PASSWORD_1.toCharArray());

    // When
    ResultActions result = mockMvc.perform(post("/api/v1/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(userCredentialDto))
        .with(csrf()));

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
  @WithMockUser
  void register_InvalidPassword_LessThan8Symbols_Failure() throws Exception {
    // Given
    UserCredentialDto userCredentialDto = new UserCredentialDto(TEST_USER_EMAIL_1,
        TEST_USER_USERNAME_1, "qwe1234".toCharArray());

    // When
    ResultActions result = mockMvc.perform(post("/api/v1/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(userCredentialDto))
        .with(csrf()));

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
  @WithMockUser
  void register_InvalidPassword_WithoutLetters_Failure() throws Exception {
    // Given
    UserCredentialDto userCredentialDto = new UserCredentialDto(TEST_USER_EMAIL_1,
        TEST_USER_USERNAME_1, "190853654".toCharArray());

    // When
    ResultActions result = mockMvc.perform(post("/api/v1/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(userCredentialDto))
        .with(csrf()));

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
  @WithMockUser
  void register_InvalidPassword_WithoutDigits_Failure() throws Exception {
    // Given
    UserCredentialDto userCredentialDto = new UserCredentialDto(TEST_USER_EMAIL_1,
        TEST_USER_USERNAME_1, "asdfghjzxcv".toCharArray());

    // When
    ResultActions result = mockMvc.perform(post("/api/v1/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(userCredentialDto))
        .with(csrf()));

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
  @WithMockUser
  void register_ExistedByEmail_Failure() throws Exception {
    // Given
    UserCredentialDto userCredentialDto = getUserCredentialDto2();
    String expectedErrorMessage = String.format(USER_EXISTS_FORMAT,
        FieldType.EMAIL.name().toLowerCase(), userCredentialDto.email());

    // When
    when(userCredentialService.register(any())).thenThrow(
        new UserAlreadyExistsException(userCredentialDto.email(), FieldType.EMAIL));

    ResultActions result = mockMvc.perform(post("/api/v1/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(userCredentialDto))
        .with(csrf()));

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
  @WithMockUser
  void register_ExistedByUsername_Failure() throws Exception {
    // Given
    UserCredentialDto userCredentialDto = getUserCredentialDto2();
    String expectedErrorMessage = String.format(USER_EXISTS_FORMAT,
        FieldType.USERNAME.name().toLowerCase(), userCredentialDto.username());

    // When
    when(userCredentialService.register(any())).thenThrow(
        new UserAlreadyExistsException(userCredentialDto.username(), FieldType.USERNAME));

    ResultActions result = mockMvc.perform(post("/api/v1/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(userCredentialDto))
        .with(csrf()));

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
  @WithMockUser
  void validateToken_Authenticated_Success() throws Exception {
    // Given
    AuthenticationResponseDto expectedResult = getAuthenticationResponseDto1();

    // When
    when(userCredentialService.validate(any())).thenReturn(expectedResult);

    ResultActions result = mockMvc.perform(get("/api/v1/auth/validate")
        .contentType(MediaType.APPLICATION_JSON)
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TEST_USER_JWT_1)
        .with(csrf()));

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
  @WithMockUser
  void validateToken_Unauthenticated_Success() throws Exception {
    // Given
    AuthenticationResponseDto expectedResult = new AuthenticationResponseDto(false);

    // When
    when(userCredentialService.validate(any())).thenReturn(expectedResult);

    ResultActions result = mockMvc.perform(get("/api/v1/auth/validate")
        .contentType(MediaType.APPLICATION_JSON)
        .with(csrf()));

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
}
