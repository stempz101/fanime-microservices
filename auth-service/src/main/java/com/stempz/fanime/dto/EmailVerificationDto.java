package com.stempz.fanime.dto;

import java.util.UUID;

public record EmailVerificationDto(String email, UUID verificationToken) {

}
