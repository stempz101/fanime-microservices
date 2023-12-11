package org.stempz.fanime.animeservice.dto;

import jakarta.validation.constraints.NotBlank;

public record GenreDto(
    String id,
    @NotBlank(message = "{not-blank.name}") String name
) {

}
