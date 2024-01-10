package com.stempz.fanime.test.utils;

import java.time.LocalDate;
import java.util.List;
import com.stempz.fanime.dto.AnimeDto;
import com.stempz.fanime.dto.AnimeSaveDto;
import com.stempz.fanime.model.Anime;
import com.stempz.fanime.model.Genre;
import com.stempz.fanime.model.Studio;
import com.stempz.fanime.model.enums.Season;
import com.stempz.fanime.model.enums.Status;

public class AnimeTestUtil {

  public static final String TEST_ANIME_ID_1 = "1";
  public static final String TEST_ANIME_TITLE_1 = "Jujutsu Kaisen";
  public static final Status TEST_ANIME_STATUS_1 = Status.RELEASING;
  public static final String TEST_ANIME_STATUS_NAME_1 = TEST_ANIME_STATUS_1.name();
  public static final String TEST_ANIME_DESCRIPTION_1 = """
      A boy fights... for "the right death."
            
      Hardship, regret, shame: the negative feelings that humans feel become Curses that lurk in our everyday lives. The Curses run rampant throughout the world, capable of leading people to terrible misfortune and even death. What's more, the Curses can only be exorcised by another Curse.
            
      Itadori Yuji is a boy with tremendous physical strength, though he lives a completely ordinary high school life. One day, to save a friend who has been attacked by Curses, he eats the finger of the Double-Faced Specter, taking the Curse into his own soul. From then on, he shares one body with the Double-Faced Specter. Guided by the most powerful of sorcerers, Gojou Satoru, Itadori is admitted to the Tokyo Metropolitan Technical High School of Sorcery, an organization that fights the Curses... and thus begins the heroic tale of a boy who became a Curse to exorcise a Curse, a life from which he could never turn back.
      """;
  public static final List<Genre> TEST_ANIME_GENRES_1 = List.of(
      GenreTestUtil.getGenre1(), GenreTestUtil.getGenre2());
  public static final List<String> TEST_ANIME_GENRE_NAMES_1 = TEST_ANIME_GENRES_1.stream()
      .map(Genre::getName)
      .toList();
  public static final Studio TEST_ANIME_STUDIO_1 = StudioTestUtil.getStudio2();
  public static final String TEST_ANIME_STUDIO_NAME_1 = TEST_ANIME_STUDIO_1.getName();
  public static final LocalDate TEST_ANIME_START_DATE_1 = LocalDate.of(2020, 11, 3);
  public static final LocalDate TEST_ANIME_END_DATE_1 = LocalDate.of(2021, 3, 27);
  public static final Season TEST_ANIME_SEASON_1 = Season.FALL;
  public static final int TEST_ANIME_SEASON_YEAR_1 = 2020;
  public static final int TEST_ANIME_TOTAL_EPISODES_1 = 24;
  public static final int TEST_ANIME_AIRED_EPISODES_1 = 24;
  public static final int TEST_ANIME_DURATION_1 = 24;

  public static Anime getAnime1() {
    return Anime.builder()
        .id(TEST_ANIME_ID_1)
        .title(TEST_ANIME_TITLE_1)
        .status(TEST_ANIME_STATUS_1)
        .description(TEST_ANIME_DESCRIPTION_1)
        .genres(TEST_ANIME_GENRES_1)
        .studio(TEST_ANIME_STUDIO_1)
        .startDate(TEST_ANIME_START_DATE_1)
        .endDate(TEST_ANIME_END_DATE_1)
        .season(TEST_ANIME_SEASON_1)
        .seasonYear(TEST_ANIME_SEASON_YEAR_1)
        .totalEpisodes(TEST_ANIME_TOTAL_EPISODES_1)
        .airedEpisodes(TEST_ANIME_AIRED_EPISODES_1)
        .duration(TEST_ANIME_DURATION_1)
        .build();
  }

  public static Anime getAnimeFromAnimeSaveDto1() {
    return Anime.builder()
        .title(TEST_ANIME_TITLE_1)
        .status(TEST_ANIME_STATUS_1)
        .description(TEST_ANIME_DESCRIPTION_1)
        .genres(TEST_ANIME_GENRES_1)
        .studio(TEST_ANIME_STUDIO_1)
        .startDate(TEST_ANIME_START_DATE_1)
        .endDate(TEST_ANIME_END_DATE_1)
        .season(TEST_ANIME_SEASON_1)
        .seasonYear(TEST_ANIME_SEASON_YEAR_1)
        .totalEpisodes(TEST_ANIME_TOTAL_EPISODES_1)
        .airedEpisodes(TEST_ANIME_AIRED_EPISODES_1)
        .duration(TEST_ANIME_DURATION_1)
        .build();
  }

  public static AnimeDto getAnimeDto1() {
    return AnimeDto.builder()
        .id(TEST_ANIME_ID_1)
        .title(TEST_ANIME_TITLE_1)
        .status(TEST_ANIME_STATUS_1)
        .description(TEST_ANIME_DESCRIPTION_1)
        .genres(TEST_ANIME_GENRES_1)
        .studio(TEST_ANIME_STUDIO_1)
        .startDate(TEST_ANIME_START_DATE_1)
        .endDate(TEST_ANIME_END_DATE_1)
        .season(TEST_ANIME_SEASON_1)
        .seasonYear(TEST_ANIME_SEASON_YEAR_1)
        .totalEpisodes(TEST_ANIME_TOTAL_EPISODES_1)
        .airedEpisodes(TEST_ANIME_AIRED_EPISODES_1)
        .duration(TEST_ANIME_DURATION_1)
        .build();
  }

  public static AnimeSaveDto getAnimeSaveDto1() {
    return AnimeSaveDto.builder()
        .title(TEST_ANIME_TITLE_1)
        .description(TEST_ANIME_DESCRIPTION_1)
        .genres(TEST_ANIME_GENRE_NAMES_1)
        .studio(TEST_ANIME_STUDIO_NAME_1)
        .seasonYear(TEST_ANIME_SEASON_YEAR_1)
        .startDate(TEST_ANIME_START_DATE_1)
        .endDate(TEST_ANIME_END_DATE_1)
        .totalEpisodes(TEST_ANIME_TOTAL_EPISODES_1)
        .airedEpisodes(TEST_ANIME_AIRED_EPISODES_1)
        .duration(TEST_ANIME_DURATION_1)
        .build();
  }
}
