package org.stempz.fanime.animeservice.controller.open;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.stempz.fanime.animeservice.model.Studio;
import org.stempz.fanime.animeservice.service.StudioService;

@RestController
@RequestMapping("/api/v1/studios")
@RequiredArgsConstructor
public class StudioController {

  private final StudioService studioService;

  @GetMapping
  public List<Studio> getAll() {
    return studioService.getAll();
  }
}
