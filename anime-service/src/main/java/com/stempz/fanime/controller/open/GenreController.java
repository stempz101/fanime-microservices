package com.stempz.fanime.controller.open;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.stempz.fanime.model.Genre;
import com.stempz.fanime.service.GenreService;

@RestController
@RequestMapping("/api/v1/genres")
@RequiredArgsConstructor
public class GenreController {

  private final GenreService genreService;

  @GetMapping
  public List<Genre> getAll() {
    return genreService.getAll();
  }
}
