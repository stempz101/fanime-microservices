package com.stempz.fanime.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import com.stempz.fanime.dto.GenreDto;
import com.stempz.fanime.model.Genre;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface GenreMapper {

  Genre toGenre(GenreDto genreDto);
}
