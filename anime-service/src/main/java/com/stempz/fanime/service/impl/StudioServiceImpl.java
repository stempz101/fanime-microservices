package com.stempz.fanime.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import com.stempz.fanime.dto.StudioDto;
import com.stempz.fanime.exception.StudioExistsException;
import com.stempz.fanime.mapper.StudioMapper;
import com.stempz.fanime.model.Studio;
import com.stempz.fanime.repo.StudioRepo;
import com.stempz.fanime.service.StudioService;

@Service
@RequiredArgsConstructor
public class StudioServiceImpl implements StudioService {

  private final StudioRepo studioRepo;
  private final StudioMapper studioMapper;

  public List<Studio> getAll() {
    return studioRepo.findAll(Sort.by(Direction.ASC, "name"));
  }

  public Studio create(StudioDto studioDto) {
    if (studioRepo.existsByNameIgnoreCase(studioDto.getName())) {
      throw new StudioExistsException(studioDto.getName());
    }

    Studio studio = studioMapper.toStudio(studioDto);
    return studioRepo.save(studio);
  }
}
