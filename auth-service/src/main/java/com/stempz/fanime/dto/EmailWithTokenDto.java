package com.stempz.fanime.dto;

import java.util.UUID;

public record EmailWithTokenDto(String email, UUID token) {

}
