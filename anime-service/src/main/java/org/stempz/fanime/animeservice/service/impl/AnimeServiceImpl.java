package org.stempz.fanime.animeservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.stempz.fanime.animeservice.mapper.AnimeMapper;
import org.stempz.fanime.animeservice.repo.AnimeRepo;
import org.stempz.fanime.animeservice.service.AnimeService;

@Service
@RequiredArgsConstructor
public class AnimeServiceImpl implements AnimeService {

  private final AnimeRepo animeRepo;
  private final AnimeMapper animeMapper;

}
