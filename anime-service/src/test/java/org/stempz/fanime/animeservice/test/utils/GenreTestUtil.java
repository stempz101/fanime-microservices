package org.stempz.fanime.animeservice.test.utils;

import java.util.List;
import org.stempz.fanime.animeservice.dto.GenreDto;
import org.stempz.fanime.animeservice.model.Genre;

public class GenreTestUtil {

  public static final String TEST_GENRE_ID_1 = "1";
  public static final String TEST_GENRE_NAME_1 = "Action";

  public static final String TEST_GENRE_ID_2 = "2";
  public static final String TEST_GENRE_NAME_2 = "Adventure";

  public static final String TEST_GENRE_ID_3 = "3";
  public static final String TEST_GENRE_NAME_3 = "Drama";

  public static Genre getGenre1() {
    return new Genre(TEST_GENRE_ID_1, TEST_GENRE_NAME_1);
  }

  public static Genre getGenre2() {
    return new Genre(TEST_GENRE_ID_2, TEST_GENRE_NAME_2);
  }

  public static Genre getGenre3() {
    return new Genre(TEST_GENRE_ID_3, TEST_GENRE_NAME_3);
  }

  public static Genre getGenreWithoutId1() {
    return new Genre(null, TEST_GENRE_NAME_1);
  }

  public static GenreDto getGenreDto1() {
    return new GenreDto(TEST_GENRE_NAME_1);
  }

  public static GenreDto getGenreDto2() {
    return new GenreDto(TEST_GENRE_NAME_2);
  }

  public static GenreDto getGenreDto3() {
    return new GenreDto(TEST_GENRE_NAME_3);
  }

  public static GenreDto getGenreDtoWithNullName() {
    return new GenreDto(null);
  }

  public static GenreDto getGenreDtoWithEmptyName() {
    return new GenreDto("");
  }

  public static List<Genre> getGenreList() {
    return List.of(getGenre1(), getGenre2(), getGenre3());
  }

  public static List<String> getGenreNameList() {
    return List.of(getGenre1().getName(), getGenre2().getName(), getGenre3().getName());
  }
}
