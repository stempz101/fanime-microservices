package org.stempz.fanime.animeservice.dto;

import jakarta.validation.constraints.NotBlank;

public record StudioDto(@NotBlank(message = "{not-blank.name}") String name) {

}
