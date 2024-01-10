package com.stempz.fanime.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class AnimeSaveDto {

  @NotBlank(message = "{not-blank.title}")
  private String title;
  private String description;
  @NotEmpty(message = "{not-null.genres}")
  private List<String> genres;
  @NotBlank(message = "{not-blank.studio}")
  private String studio;
  private Integer seasonYear;
  private LocalDate startDate;
  private LocalDate endDate;
  private String status;
  private int totalEpisodes;
  private int airedEpisodes;
  private Integer duration;
//    private MultipartFile coverImage;
//    private MultipartFile bannerImage;
}
