package com.stempz.fanime.service;

import java.util.List;
import com.stempz.fanime.dto.GenreDto;
import com.stempz.fanime.model.Genre;

public interface GenreService {

  List<Genre> getAll();

  Genre create(GenreDto genreDto);
}
