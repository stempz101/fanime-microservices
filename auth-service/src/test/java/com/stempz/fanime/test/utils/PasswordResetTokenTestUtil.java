package com.stempz.fanime.test.utils;

import com.stempz.fanime.dto.ResetPasswordDto;
import com.stempz.fanime.model.PasswordResetToken;
import com.stempz.fanime.model.UserCredential;
import java.time.LocalDateTime;
import java.util.UUID;

public class PasswordResetTokenTestUtil {

  public static final long TEST_PRT_ID_1 = 1;
  public static final UUID TEST_PRT_TOKEN_1 = UUID.randomUUID();
  public static final UserCredential TEST_PRT_USER_1 = UserCredentialTestUtil.getUserCredential1();
  public static final LocalDateTime TEST_PRT_EXPIRATION_TIME_1 = LocalDateTime.now().plusDays(1);

  public static PasswordResetToken getPasswordResetToken1() {
    return PasswordResetToken.builder()
        .id(TEST_PRT_ID_1)
        .token(TEST_PRT_TOKEN_1)
        .user(TEST_PRT_USER_1)
        .expirationTime(TEST_PRT_EXPIRATION_TIME_1)
        .build();
  }

  public static ResetPasswordDto getResetPasswordDto1() {
    return new ResetPasswordDto(UserCredentialTestUtil.TEST_USER_PASSWORD_DECRYPTED_1.toCharArray(),
        TEST_PRT_TOKEN_1.toString());
  }
}
