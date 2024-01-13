package com.stempz.fanime.dto;

import com.stempz.fanime.dto.validation.annotation.Password;

public record ResetPasswordDto(
    @Password char[] password,
    String token
) {

}
