package com.stempz.fanime.service;

import java.util.List;
import com.stempz.fanime.dto.StudioDto;
import com.stempz.fanime.model.Studio;

public interface StudioService {

  List<Studio> getAll();

  Studio create(StudioDto studioDto);
}
