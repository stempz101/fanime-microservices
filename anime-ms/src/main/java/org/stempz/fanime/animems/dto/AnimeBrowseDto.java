package org.stempz.fanime.animems.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class AnimeBrowseDto {
  private List<AnimeDto> trending;
  private List<AnimeDto> popularThisSeason;
  private List<AnimeDto> upcoming;
  private List<AnimeDto> allTimePopular;
  private List<AnimeDto> top;
}
