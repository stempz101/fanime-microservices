package org.stempz.fanime.animeservice.controller.open;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.stempz.fanime.animeservice.dto.AnimeItemDto;

@RestController
@RequestMapping("/api/v1/anime")
@RequiredArgsConstructor
public class AnimeController {

  @GetMapping
  public AnimeItemDto browse() {
    return null;
  }
}
