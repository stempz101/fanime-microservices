package com.stempz.fanime.service;

import jakarta.servlet.http.HttpServletRequest;
import com.stempz.fanime.dto.AuthenticationRequestDto;
import com.stempz.fanime.dto.AuthenticationResponseDto;
import com.stempz.fanime.dto.UserCredentialDto;

public interface UserCredentialService {

  AuthenticationResponseDto authenticate(AuthenticationRequestDto authDto);

  AuthenticationResponseDto register(UserCredentialDto userCredentialDto);

  void verify(String token);

  AuthenticationResponseDto validate(HttpServletRequest request);
}
