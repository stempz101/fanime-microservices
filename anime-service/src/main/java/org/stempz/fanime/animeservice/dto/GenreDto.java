package org.stempz.fanime.animeservice.dto;

import jakarta.validation.constraints.NotBlank;

public record GenreDto(@NotBlank(message = "{not-blank.name}") String name) {

}
