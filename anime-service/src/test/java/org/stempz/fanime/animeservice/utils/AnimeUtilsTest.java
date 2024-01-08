package org.stempz.fanime.animeservice.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.stempz.fanime.animeservice.test.utils.AnimeTestUtil.getAnime1;

import java.time.LocalDate;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.stempz.fanime.animeservice.model.Anime;
import org.stempz.fanime.animeservice.model.enums.Season;
import org.stempz.fanime.animeservice.model.enums.Status;

public class AnimeUtilsTest {

  @ParameterizedTest
  @CsvSource({
      "12, 24, RELEASING", "1, 12, RELEASING", "23, 24, RELEASING",
      "8, 8, FINISHED",
      "0, 12, NOT_YET_RELEASED",
  })
  void setStatusIfNull_CorrectStatusBasedOnAiredAndTotalEpisodes_Success(
      int airedEpisodes, int totalEpisodes, Status expectedStatus) {
    // Given
    Anime anime = getAnime1();
    anime.setStatus(null);
    anime.setTotalEpisodes(totalEpisodes);
    anime.setAiredEpisodes(airedEpisodes);

    // When
    AnimeUtils.setStatusIfNull(anime);

    // Then
    assertEquals(anime.getStatus(), expectedStatus);
  }

  @ParameterizedTest
  @CsvSource({
      "12, WINTER", "1, WINTER", "2, WINTER",
      "3, SPRING", "4, SPRING", "5, SPRING",
      "6, SUMMER", "7, SUMMER", "8, SUMMER",
      "9, FALL", "10, FALL", "11, FALL"
  })
  void setSeason_CorrectSeasonBasedOnStartMonth_Success(int startMonth, Season expectedSeason) {
    // Given
    Anime anime = getAnime1();
    anime.setSeason(null);
    anime.setStartDate(LocalDate.of(2020, startMonth, 1));

    // When
    AnimeUtils.setSeason(anime);

    // Then
    assertEquals(anime.getSeason(), expectedSeason);
  }

  @ParameterizedTest
  @CsvSource({
      "2020-10-11, 2020", "2001-09-14, 2001", "1991-05-23, 1991",
      "1975-07-07, 1975", "2011-01-01, 2011", "2006-12-31, 2006"
  })
  void setSeasonYearIfNull_CorrectSeasonYearBasedOnStartDate_Success(
      LocalDate startDate, int expectedSeasonYear) {
    // Given
    Anime anime = getAnime1();
    anime.setSeasonYear(null);
    anime.setStartDate(startDate);

    // When
    AnimeUtils.setSeasonYearIfNull(anime);

    // Then
    assertEquals(anime.getSeasonYear(), expectedSeasonYear);
  }
}
