package org.stempz.fanime.animeservice.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.stempz.fanime.animeservice.dto.GenreDto;
import org.stempz.fanime.animeservice.exception.GenreExistsException;
import org.stempz.fanime.animeservice.mapper.GenreMapper;
import org.stempz.fanime.animeservice.model.Genre;
import org.stempz.fanime.animeservice.repo.GenreRepo;
import org.stempz.fanime.animeservice.service.GenreService;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {

  private final GenreRepo genreRepo;
  private final GenreMapper genreMapper;

  public List<Genre> getAll() {
    return genreRepo.findAll(Sort.by(Direction.ASC, "name"));
  }

  public Genre create(GenreDto genreDto) {
    if (genreRepo.existsByNameIgnoreCase(genreDto.name())) {
      throw new GenreExistsException(genreDto.name());
    }

    Genre genre = genreMapper.toGenre(genreDto);
    return genreRepo.save(genre);
  }
}
