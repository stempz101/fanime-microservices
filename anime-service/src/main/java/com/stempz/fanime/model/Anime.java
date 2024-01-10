package com.stempz.fanime.model;

import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import com.stempz.fanime.model.enums.Season;
import com.stempz.fanime.model.enums.Status;

@Document(collection = "anime")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Anime {

  @Id
  private String id;
  @Indexed
  private String title;
  private Status status;
  private String description;
  @DBRef
  private List<Genre> genres;
  @DBRef
  private Studio studio;
  private LocalDate startDate;
  private LocalDate endDate;
  private Season season;
  private Integer seasonYear;
  private int totalEpisodes;
  private int airedEpisodes;
  private Integer duration;
  private String coverImage;
  private String bannerImage;
}
