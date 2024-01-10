package com.stempz.fanime.controller.secured;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.stempz.fanime.dto.GenreDto;
import com.stempz.fanime.model.Genre;
import com.stempz.fanime.service.GenreService;

@RestController
@RequestMapping("/api/v1/secured/genres")
@RequiredArgsConstructor
public class GenreSecuredController {

  private final GenreService genreService;

  @PostMapping
  public Genre create(@RequestBody @Valid GenreDto genreDto) {
    return genreService.create(genreDto);
  }
}
