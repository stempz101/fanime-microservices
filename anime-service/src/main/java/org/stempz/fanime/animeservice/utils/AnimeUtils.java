package org.stempz.fanime.animeservice.utils;

import java.time.Month;
import lombok.experimental.UtilityClass;
import org.stempz.fanime.animeservice.model.Anime;
import org.stempz.fanime.animeservice.model.enums.Season;
import org.stempz.fanime.animeservice.model.enums.Status;

@UtilityClass
public class AnimeUtils {

  public void setStatusIfNull(Anime anime) {
    if (anime.getStatus() == null) {
      if (anime.getAiredEpisodes() > 0 ) {
        if (anime.getAiredEpisodes() < anime.getTotalEpisodes()) {
          anime.setStatus(Status.RELEASING);
        } else {
          anime.setStatus(Status.FINISHED);
        }
      } else {
        anime.setStatus(Status.NOT_YET_RELEASED);
      }
    }
  }

  public void setSeason(Anime anime) {
    if (anime.getStartDate() != null) {
      Month startMonth = anime.getStartDate().getMonth();

      if (Season.WINTER.contains(startMonth)) {
        anime.setSeason(Season.WINTER);
      } else if (Season.SPRING.contains(startMonth)) {
        anime.setSeason(Season.SPRING);
      } else if (Season.SUMMER.contains(startMonth)) {
        anime.setSeason(Season.SUMMER);
      } else {
        anime.setSeason(Season.FALL);
      }
    }
  }

  public void setSeasonYearIfNull(Anime anime) {
    if (anime.getSeasonYear() == null) {
      if (anime.getStartDate() != null) {
        anime.setSeasonYear(anime.getStartDate().getYear());
      }
    }
  }
}
