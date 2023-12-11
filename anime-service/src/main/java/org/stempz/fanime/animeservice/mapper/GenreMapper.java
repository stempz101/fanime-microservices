package org.stempz.fanime.animeservice.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.stempz.fanime.animeservice.dto.GenreDto;
import org.stempz.fanime.animeservice.model.Genre;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface GenreMapper {

  Genre toGenre(GenreDto genreDto);
}
