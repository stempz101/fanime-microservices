package org.stempz.fanime.animeservice.service;

import java.util.List;
import org.stempz.fanime.animeservice.dto.StudioDto;
import org.stempz.fanime.animeservice.model.Studio;

public interface StudioService {

  List<Studio> getAll();

  Studio create(StudioDto studioDto);
}
