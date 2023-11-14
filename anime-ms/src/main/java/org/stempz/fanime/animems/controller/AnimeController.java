package org.stempz.fanime.animems.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.stempz.fanime.animems.dto.AnimeBrowseDto;

@RestController
@RequestMapping("/api/v1/animes")
public class AnimeController {

  @GetMapping
  public AnimeBrowseDto browse() {
    return null;
  }
}
