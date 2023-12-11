package org.stempz.fanime.animeservice.service;

import java.util.List;
import org.stempz.fanime.animeservice.dto.GenreDto;
import org.stempz.fanime.animeservice.model.Genre;

public interface GenreService {

  List<Genre> getAll();

  Genre create(GenreDto genreDto);
}
