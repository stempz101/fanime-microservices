package org.stempz.fanime.service;

import jakarta.servlet.http.HttpServletRequest;
import org.stempz.fanime.dto.AuthenticationRequestDto;
import org.stempz.fanime.dto.AuthenticationResponseDto;
import org.stempz.fanime.dto.UserCredentialDto;

public interface UserCredentialService {

  AuthenticationResponseDto authenticate(AuthenticationRequestDto authDto);

  AuthenticationResponseDto register(UserCredentialDto userCredentialDto);

  AuthenticationResponseDto validate(HttpServletRequest request);
}
