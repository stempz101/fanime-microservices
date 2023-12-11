package org.stempz.fanime.animeservice.dto;

import java.util.List;
import org.stempz.fanime.animeservice.model.Genre;
import org.stempz.fanime.animeservice.model.Studio;
import org.stempz.fanime.animeservice.model.enums.Status;

public record AnimeItemDto(
    String id,
    String title,
    Status status,
    Studio studio,
    int episodes,
    List<Genre> genres,
    String coverImage,
    int avgRating
) {

}
