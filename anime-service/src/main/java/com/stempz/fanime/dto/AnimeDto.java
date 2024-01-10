package com.stempz.fanime.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.stempz.fanime.model.Genre;
import com.stempz.fanime.model.Studio;
import com.stempz.fanime.model.enums.Season;
import com.stempz.fanime.model.enums.Status;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class AnimeDto {

  private String id;
  private String title;
  private Status status;
  private String description;
  private List<Genre> genres;
  private Studio studio;
  private LocalDate startDate;
  private LocalDate endDate;
  private Season season;
  private int seasonYear;
  private int totalEpisodes;
  private int airedEpisodes;
  private int duration;
//    String coverImage,
//    String bannerImage
}
