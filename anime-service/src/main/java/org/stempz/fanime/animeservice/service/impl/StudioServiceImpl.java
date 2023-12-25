package org.stempz.fanime.animeservice.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.stempz.fanime.animeservice.dto.StudioDto;
import org.stempz.fanime.animeservice.exception.StudioExistsException;
import org.stempz.fanime.animeservice.mapper.StudioMapper;
import org.stempz.fanime.animeservice.model.Studio;
import org.stempz.fanime.animeservice.repo.StudioRepo;
import org.stempz.fanime.animeservice.service.StudioService;

@Service
@RequiredArgsConstructor
public class StudioServiceImpl implements StudioService {

  private final StudioRepo studioRepo;
  private final StudioMapper studioMapper;

  public List<Studio> getAll() {
    return studioRepo.findAll(Sort.by(Direction.ASC, "name"));
  }

  public Studio create(StudioDto studioDto) {
    if (studioRepo.existsByNameIgnoreCase(studioDto.name())) {
      throw new StudioExistsException(studioDto.name());
    }

    Studio studio = studioMapper.toStudio(studioDto);
    return studioRepo.save(studio);
  }
}
