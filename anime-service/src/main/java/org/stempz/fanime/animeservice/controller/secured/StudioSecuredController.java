package org.stempz.fanime.animeservice.controller.secured;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.stempz.fanime.animeservice.dto.StudioDto;
import org.stempz.fanime.animeservice.model.Studio;
import org.stempz.fanime.animeservice.service.StudioService;

@RestController
@RequestMapping("/api/v1/secured/studios")
@RequiredArgsConstructor
public class StudioSecuredController {

  private final StudioService studioService;

  @PostMapping
  public Studio create(@RequestBody @Valid StudioDto studioDto) {
    return studioService.create(studioDto);
  }
}
