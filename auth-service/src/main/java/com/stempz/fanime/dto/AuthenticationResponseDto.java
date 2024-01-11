package com.stempz.fanime.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Builder;
import com.stempz.fanime.model.enums.Role;

@Builder
@JsonInclude(Include.NON_NULL)
public record AuthenticationResponseDto(
    boolean authenticated,
    Long id,
    String email,
    String username,
    String jwt,
    Role role,
    Boolean verified
) {

  public AuthenticationResponseDto(boolean authenticated) {
    this(authenticated, null, null, null, null, null, null);
  }
}
