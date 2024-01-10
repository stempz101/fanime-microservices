package com.stempz.fanime.controller.secured;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.stempz.fanime.dto.StudioDto;
import com.stempz.fanime.model.Studio;
import com.stempz.fanime.service.StudioService;

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
