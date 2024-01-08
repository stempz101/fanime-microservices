package org.stempz.fanime.animeservice.controller.secured;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.stempz.fanime.animeservice.dto.AnimeDto;
import org.stempz.fanime.animeservice.dto.AnimeSaveDto;
import org.stempz.fanime.animeservice.service.AnimeService;
import org.stempz.fanime.animeservice.validator.AnimeSaveDtoValidator;

@RestController
@RequestMapping("/api/v1/secured/anime")
@RequiredArgsConstructor
public class AnimeSecuredController {

  private final AnimeService animeService;
  private final AnimeSaveDtoValidator animeSaveDtoValidator;

  @PostMapping
  public AnimeDto create(@RequestBody @Valid AnimeSaveDto animeSaveDto) {
    animeSaveDtoValidator.validate(animeSaveDto);

    return animeService.create(animeSaveDto);
  }
}
