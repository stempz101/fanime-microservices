package org.stempz.fanime.animeservice.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.stempz.fanime.animeservice.dto.AnimeItemDto;
import org.stempz.fanime.animeservice.model.Anime;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface AnimeMapper {

  @BeanMapping(ignoreByDefault = true)
  @Mapping(source = "id", target = "id")
  @Mapping(source = "title", target = "title")
  @Mapping(source = "status", target = "status")
  @Mapping(source = "genres", target = "genres")
  @Mapping(source = "studio", target = "studio")
  @Mapping(source = "episodes", target = "episodes")
  AnimeItemDto toAnimeItemDto(Anime anime);
}
