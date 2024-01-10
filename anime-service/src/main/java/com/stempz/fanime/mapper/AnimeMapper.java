package com.stempz.fanime.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import com.stempz.fanime.dto.AnimeDto;
import com.stempz.fanime.dto.AnimeItemDto;
import com.stempz.fanime.dto.AnimeSaveDto;
import com.stempz.fanime.model.Anime;
import com.stempz.fanime.model.enums.Status;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface AnimeMapper {

  @BeanMapping(ignoreByDefault = true)
  @Mapping(source = "title", target = "title")
  @Mapping(source = "status", target = "status", qualifiedByName = "getAnimeStatus")
  @Mapping(source = "description", target = "description", qualifiedByName = "getAnimeDescription")
  @Mapping(source = "startDate", target = "startDate")
  @Mapping(source = "endDate", target = "endDate")
  @Mapping(source = "totalEpisodes", target = "totalEpisodes")
  @Mapping(source = "airedEpisodes", target = "airedEpisodes")
  @Mapping(source = "duration", target = "duration", qualifiedByName = "getAnimeDuration")
  Anime toAnime(AnimeSaveDto animeSaveDto);

  AnimeDto toAnimeDto(Anime anime);

  @BeanMapping(ignoreByDefault = true)
  @Mapping(source = "id", target = "id")
  @Mapping(source = "title", target = "title")
  @Mapping(source = "status", target = "status")
  @Mapping(source = "genres", target = "genres")
  @Mapping(source = "studio", target = "studio")
  @Mapping(source = "totalEpisodes", target = "totalEpisodes")
  @Mapping(source = "airedEpisodes", target = "airedEpisodes")
  AnimeItemDto toAnimeItemDto(Anime anime);

  @Named("getAnimeStatus")
  default Status getAnimeStatus(String status) {
    return !status.isBlank() ? Enum.valueOf(Status.class, status) : null;
  }

  @Named("getAnimeDescription")
  default String getAnimeDescription(String description) {
    return !description.isBlank() ? description : null;
  }

  @Named("getAnimeDuration")
  default Integer getAnimeDuration(Integer duration) {
    return duration != null && duration > 0 ? duration : null;
  }
}
