package org.stempz.fanime.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.stempz.fanime.dto.validation.annotation.Password;

public record UserCredentialDto(
    @NotBlank(message = "{not-blank.email}") @Email(message = "{email.message}") String email,
    @NotBlank(message = "{not-blank.username}") String username,
    @Password char[] password
) {

}
