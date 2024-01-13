package com.stempz.fanime.service;

import com.stempz.fanime.dto.ResetPasswordDto;
import com.stempz.fanime.dto.UserEmailDto;
import jakarta.servlet.http.HttpServletRequest;
import com.stempz.fanime.dto.AuthenticationRequestDto;
import com.stempz.fanime.dto.AuthenticationResponseDto;
import com.stempz.fanime.dto.UserCredentialDto;

public interface UserCredentialService {

  AuthenticationResponseDto authenticate(AuthenticationRequestDto authDto);

  AuthenticationResponseDto register(UserCredentialDto userCredentialDto);

  AuthenticationResponseDto validate(HttpServletRequest request);

  void verify(String token);

  void forgotPassword(UserEmailDto userEmailDto);

  void resetPassword(ResetPasswordDto resetPasswordDto);
}
