package com.stempz.fanime.controller.secured;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.stempz.fanime.dto.AnimeDto;
import com.stempz.fanime.dto.AnimeSaveDto;
import com.stempz.fanime.service.AnimeService;
import com.stempz.fanime.validator.AnimeSaveDtoValidator;

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
