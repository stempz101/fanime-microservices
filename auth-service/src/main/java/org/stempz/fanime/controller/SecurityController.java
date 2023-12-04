package org.stempz.fanime.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.stempz.fanime.dto.AuthenticationRequestDto;
import org.stempz.fanime.dto.AuthenticationResponseDto;
import org.stempz.fanime.dto.UserCredentialDto;
import org.stempz.fanime.service.UserCredentialService;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class SecurityController {

  private final UserCredentialService userCredentialService;

  @PostMapping("/authenticate")
  public AuthenticationResponseDto authenticate(
      @RequestBody AuthenticationRequestDto authDto) {
    return userCredentialService.authenticate(authDto);
  }

  @PostMapping("/register")
  public AuthenticationResponseDto register(@RequestBody @Valid UserCredentialDto userCredentialDto) {
    return userCredentialService.register(userCredentialDto);
  }

  @GetMapping("/validate")
  public AuthenticationResponseDto validateToken(HttpServletRequest request) {
    return userCredentialService.validate(request);
  }
}
