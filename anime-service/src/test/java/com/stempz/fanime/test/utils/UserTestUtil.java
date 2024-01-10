package com.stempz.fanime.test.utils;

import java.util.Map;
import com.stempz.fanime.model.enums.Role;
import com.stempz.fanime.model.security.User;

public class UserTestUtil {

  public static final long TEST_USER_ID_1 = 1L;
  public static final String TEST_USER_EMAIL_1 = "testadmin@gmail.com";
  public static final Role TEST_USER_ROLE_1 = Role.ADMIN;
  public static final Map<String, Object> TEST_USER_CLAIMS_1 = Map.of(
      "userId", TEST_USER_ID_1,
      "userRole", "ROLE_" + TEST_USER_ROLE_1.name()
  );
  public static final String TEST_USER_JWT_1 = JwtUtil.generateToken(TEST_USER_CLAIMS_1, TEST_USER_EMAIL_1);

  public static final long TEST_USER_ID_2 = 2L;
  public static final String TEST_USER_EMAIL_2 = "testuser@gmail.com";
  public static final Role TEST_USER_ROLE_2 = Role.USER;
  public static final Map<String, Object> TEST_USER_CLAIMS_2 = Map.of(
      "userId", TEST_USER_ID_2,
      "userRole", "ROLE_" + TEST_USER_ROLE_2.name()
  );
  public static final String TEST_USER_JWT_2 = JwtUtil.generateToken(TEST_USER_CLAIMS_2, TEST_USER_EMAIL_2);

  public static User getUser1() {
    return new User(TEST_USER_ID_1, TEST_USER_EMAIL_1, TEST_USER_ROLE_1);
  }

  public static User getUser2() {
    return new User(TEST_USER_ID_2, TEST_USER_EMAIL_2, TEST_USER_ROLE_2);
  }
}
