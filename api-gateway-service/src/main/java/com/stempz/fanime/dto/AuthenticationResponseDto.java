package com.stempz.fanime.dto;

public record AuthenticationResponseDto(
    boolean authenticated,
    Long id,
    String email,
    String username,
    String jwt,
    String role
) {

  public AuthenticationResponseDto(boolean authenticated) {
    this(authenticated, null, null, null, null, null);
  }
}
