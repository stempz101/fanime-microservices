package org.stempz.fanime.animeservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudioDto {

  @NotBlank(message = "{not-blank.name}")
  private String name;
}
