package org.stempz.fanime.animeservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.stempz.fanime.animeservice.model.Genre;
import org.stempz.fanime.animeservice.model.Studio;
import org.stempz.fanime.animeservice.model.enums.Status;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class AnimeItemDto {

  private String id;
  private String title;
  private Status status;
  private Studio studio;
  private int totalEpisodes;
  private int airedEpisodes;
  private List<Genre> genres;
  private String coverImage;
  private int avgRating;
}
