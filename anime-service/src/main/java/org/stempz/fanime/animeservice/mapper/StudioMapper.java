package org.stempz.fanime.animeservice.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.stempz.fanime.animeservice.dto.StudioDto;
import org.stempz.fanime.animeservice.model.Studio;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface StudioMapper {

  Studio toStudio(StudioDto studioDto);
}
