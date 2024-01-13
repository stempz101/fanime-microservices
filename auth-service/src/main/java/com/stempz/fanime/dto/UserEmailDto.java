package com.stempz.fanime.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserEmailDto(
    @NotBlank(message = "{not-blank.email}") @Email(message = "{email.message}") String email
) {

}
