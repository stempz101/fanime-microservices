package com.stempz.fanime.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import com.stempz.fanime.dto.StudioDto;
import com.stempz.fanime.model.Studio;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface StudioMapper {

  Studio toStudio(StudioDto studioDto);
}
