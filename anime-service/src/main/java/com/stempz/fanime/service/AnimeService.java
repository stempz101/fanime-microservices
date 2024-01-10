package com.stempz.fanime.service;

import com.stempz.fanime.dto.AnimeDto;
import com.stempz.fanime.dto.AnimeSaveDto;

public interface AnimeService {

  AnimeDto create(AnimeSaveDto animeSaveDto);
}
