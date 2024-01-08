package org.stempz.fanime.animeservice.service;

import org.stempz.fanime.animeservice.dto.AnimeDto;
import org.stempz.fanime.animeservice.dto.AnimeSaveDto;

public interface AnimeService {

  AnimeDto create(AnimeSaveDto animeSaveDto);
}
