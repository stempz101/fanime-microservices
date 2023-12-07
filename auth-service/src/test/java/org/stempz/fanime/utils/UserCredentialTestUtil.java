package org.stempz.fanime.utils;

import java.util.Map;
import org.stempz.fanime.dto.AuthenticationRequestDto;
import org.stempz.fanime.dto.AuthenticationResponseDto;
import org.stempz.fanime.dto.UserCredentialDto;
import org.stempz.fanime.model.UserCredential;
import org.stempz.fanime.model.enums.Role;

public class UserCredentialTestUtil {

  public static final long TEST_USER_ID_1 = 1;
  public static final String TEST_USER_EMAIL_1 = "test1@gmail.com";
  public static final String TEST_USER_USERNAME_1 = "testuser1";
  public static final String TEST_USER_PASSWORD_1 = "qwerty123";
  public static final String TEST_USER_ACTIVATION_CODE_1 = "activationCode1";
  public static final Role TEST_USER_ROLE_1 = Role.USER;
  public static final Map<String, Object> TEST_USER_CLAIMS_1 = Map.of(
      "userId", TEST_USER_ID_1,
      "userRole", "ROLE_" + TEST_USER_ROLE_1.name()
  );
  public static final String TEST_USER_JWT_1 = JwtUtil.generateToken(TEST_USER_CLAIMS_1, TEST_USER_EMAIL_1);

  public static final long TEST_USER_ID_2 = 2;
  public static final String TEST_USER_EMAIL_2 = "test2@gmail.com";
  public static final String TEST_USER_USERNAME_2 = "testuser2";
  public static final String TEST_USER_PASSWORD_2 = "qwerty321";
  public static final String TEST_USER_ACTIVATION_CODE_2 = "activationCode2";

  public static UserCredential getUserCredential1() {
    UserCredential userCredential = new UserCredential();

    userCredential.setId(TEST_USER_ID_1);
    userCredential.setEmail(TEST_USER_EMAIL_1);
    userCredential.set_username(TEST_USER_USERNAME_1);
    userCredential.setPassword(TEST_USER_PASSWORD_1);
    userCredential.setActivationCode(TEST_USER_ACTIVATION_CODE_1);
    userCredential.setRole(TEST_USER_ROLE_1);

    return userCredential;
  }

  public static UserCredential getUserCredential2() {
    UserCredential userCredential = new UserCredential();

    userCredential.setId(TEST_USER_ID_2);
    userCredential.setEmail(TEST_USER_EMAIL_2);
    userCredential.set_username(TEST_USER_USERNAME_2);
    userCredential.setPassword(TEST_USER_PASSWORD_2);
    userCredential.setActivationCode(TEST_USER_ACTIVATION_CODE_2);
    userCredential.setRole(Role.USER);

    return userCredential;
  }

  public static UserCredentialDto getUserCredentialDto1() {
    return new UserCredentialDto(TEST_USER_EMAIL_1, TEST_USER_USERNAME_1,
        TEST_USER_PASSWORD_1.toCharArray());
  }

  public static UserCredentialDto getUserCredentialDto2() {
    return new UserCredentialDto(TEST_USER_EMAIL_2, TEST_USER_USERNAME_2,
        TEST_USER_PASSWORD_2.toCharArray());
  }

  public static AuthenticationRequestDto getAuthenticationRequestDto1() {
    return new AuthenticationRequestDto(TEST_USER_EMAIL_1, TEST_USER_PASSWORD_1.toCharArray());
  }

  public static AuthenticationRequestDto getAuthenticationRequestDto2() {
    return new AuthenticationRequestDto(TEST_USER_EMAIL_2, TEST_USER_PASSWORD_2.toCharArray());
  }

  public static AuthenticationResponseDto getAuthenticationResponseDto1() {
    return new AuthenticationResponseDto(true, TEST_USER_ID_1, TEST_USER_EMAIL_1,
        TEST_USER_USERNAME_1, TEST_USER_JWT_1, TEST_USER_ROLE_1);
  }
}
