package com.stempz.fanime.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import com.stempz.fanime.dto.GenreDto;
import com.stempz.fanime.exception.GenreExistsException;
import com.stempz.fanime.mapper.GenreMapper;
import com.stempz.fanime.model.Genre;
import com.stempz.fanime.repo.GenreRepo;
import com.stempz.fanime.service.GenreService;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {

  private final GenreRepo genreRepo;
  private final GenreMapper genreMapper;

  public List<Genre> getAll() {
    return genreRepo.findAll(Sort.by(Direction.ASC, "name"));
  }

  public Genre create(GenreDto genreDto) {
    if (genreRepo.existsByNameIgnoreCase(genreDto.getName())) {
      throw new GenreExistsException(genreDto.getName());
    }

    Genre genre = genreMapper.toGenre(genreDto);
    return genreRepo.save(genre);
  }
}
