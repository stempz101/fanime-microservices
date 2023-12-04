package org.stempz.fanime.dto;

public record AuthenticationRequestDto(
    String email,
    char[] password
) {

}
