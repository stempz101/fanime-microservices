package com.stempz.fanime.controller;

import com.stempz.fanime.dto.ResetPasswordDto;
import com.stempz.fanime.dto.UserEmailDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.stempz.fanime.dto.AuthenticationRequestDto;
import com.stempz.fanime.dto.AuthenticationResponseDto;
import com.stempz.fanime.dto.UserCredentialDto;
import com.stempz.fanime.service.UserCredentialService;

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

  @GetMapping("/verify")
  public void verify(@RequestParam String token) {
    userCredentialService.verify(token);
  }

  @PostMapping("/forgot-password")
  public void forgotPassword(@RequestBody @Valid UserEmailDto userEmailDto) {
    userCredentialService.forgotPassword(userEmailDto);
  }

  @PostMapping("/reset-password")
  public void resetPassword(@RequestBody @Valid ResetPasswordDto resetPasswordDto) {
    userCredentialService.resetPassword(resetPasswordDto);
  }
}
