package com.stempz.fanime.test.utils;

import com.stempz.fanime.model.PasswordRecord;
import com.stempz.fanime.model.UserCredential;
import java.util.List;

public class PasswordRecordTestUtil {

  public static final long TEST_PR_ID_1 = 1;
  public static final String TEST_PR_PASSWORD_1 = UserCredentialTestUtil.getUserCredential1().getPassword();

  public static final long TEST_PR_ID_2 = 2;
  public static final String TEST_PR_PASSWORD_2 = "$2a$10$GxSsSvYdnDIRzL7h54wXJ.h3saSh3RuqxEWRGCO6lLgQOs6/ix5yu";

  public static final long TEST_PR_ID_3 = 3;
  public static final String TEST_PR_PASSWORD_3 = "$2a$10$g06ukXg82fD5jYnqMDVlTu506RfdiH/jJTRxdWM6oV1bvhXnUEThm";

  public static final UserCredential TEST_PR_USER = UserCredentialTestUtil.getUserCredential1();

  public static PasswordRecord getPasswordRecord1() {
    return PasswordRecord.builder()
        .id(TEST_PR_ID_1)
        .password(TEST_PR_PASSWORD_1)
        .user(TEST_PR_USER)
        .build();
  }

  public static PasswordRecord getPasswordRecord2() {
    return PasswordRecord.builder()
        .id(TEST_PR_ID_2)
        .password(TEST_PR_PASSWORD_2)
        .user(TEST_PR_USER)
        .build();
  }

  public static PasswordRecord getPasswordRecord3() {
    return PasswordRecord.builder()
        .id(TEST_PR_ID_3)
        .password(TEST_PR_PASSWORD_3)
        .user(TEST_PR_USER)
        .build();
  }

  public static List<PasswordRecord> getPasswordRecordList() {
    return List.of(getPasswordRecord1(), getPasswordRecord2());
  }
}
